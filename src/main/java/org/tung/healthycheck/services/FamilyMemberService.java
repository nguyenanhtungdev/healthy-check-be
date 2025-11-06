package org.tung.healthycheck.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tung.healthycheck.dto.FamilyMemberDTO;
import org.tung.healthycheck.dto.UserHealthDTO;
import org.tung.healthycheck.dto.UserSimpleDTO;
import org.tung.healthycheck.model.FamilyMember;
import org.tung.healthycheck.model.User;
import org.tung.healthycheck.repository.FamilyMemberRepository;
import org.tung.healthycheck.repository.UserRepository;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class FamilyMemberService {

    @Autowired
    private FamilyMemberRepository familyMemberRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Lấy danh sách thành viên (bao gồm cả chủ hộ)
     */
    public List<FamilyMemberDTO> getFamilyMembers(UUID ownerId) {
        List<FamilyMemberDTO> members = familyMemberRepository.findByOwner_Id(ownerId)
                .stream()
                .map(this::convertToDTO)
                .toList();

        Optional<User> ownerOpt = userRepository.findById(ownerId);

        List<FamilyMemberDTO> finalMembers = new ArrayList<>(members);

        if (ownerOpt.isPresent()) {
            User owner = ownerOpt.get();

            UserHealthDTO healthDTO = null;
            if (owner.getHealthInfo() != null) {
                healthDTO = new UserHealthDTO(
                        owner.getHealthInfo().getHeight(),
                        owner.getHealthInfo().getWeight(),
                        owner.getHealthInfo().getBloodType()
                );
            }

            String imagePath = null;
            if (owner.getAccount() != null) {
                imagePath = owner.getAccount().getImage();
            }

            UserSimpleDTO ownerDTO = new UserSimpleDTO();
            ownerDTO.setId(owner.getId());
            ownerDTO.setFullName(owner.getFullName());
            ownerDTO.setUrlImage(imagePath);
            ownerDTO.setPhone(owner.getPhone());
            ownerDTO.setEmail(owner.getEmail());
            ownerDTO.setBirth(owner.getBirth());
            ownerDTO.setGender(owner.getGender());
            ownerDTO.setHealthInfo(healthDTO);
            ownerDTO.setRoleInFamily("Chủ hộ");

            FamilyMemberDTO ownerFm = new FamilyMemberDTO();
            ownerFm.setRelation("Chủ hộ");
            ownerFm.setOwnerId(owner.getId());
            ownerFm.setMember(ownerDTO);

            finalMembers.add(0, ownerFm);
        }

        // Gán role cho các thành viên khác
        for (FamilyMemberDTO fm : finalMembers) {
            if (!"Chủ hộ".equals(fm.getRelation()) && fm.getMember() != null) {
                fm.getMember().setRoleInFamily("Thành viên");
            }
        }

        return finalMembers;

    }

    /**
     * Thêm thành viên bằng số điện thoại
     */
    public String addMemberByPhone(UUID ownerId, String phone, String relation) {
        Optional<User> ownerOpt = userRepository.findById(ownerId);
        if (ownerOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy chủ sở hữu tài khoản");
        }

        Optional<User> memberOpt = userRepository.findByPhone(phone);
        if (memberOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy người dùng với số điện thoại: " + phone);
        }

        User owner = ownerOpt.get();
        User member = memberOpt.get();

        if (familyMemberRepository.existsByOwner_IdAndMember_Id(ownerId, member.getId())) {
            throw new RuntimeException("Người này đã có trong danh sách gia đình");
        }

        FamilyMember familyMember = new FamilyMember();
        familyMember.setOwner(owner);
        familyMember.setMember(member);
        familyMember.setRelation(relation);

        familyMemberRepository.save(familyMember);
        return "Thêm thành viên thành công!";
    }

    @Transactional
    public void deleteMemberByUserId(UUID memberId) {
        familyMemberRepository.deleteByMember_Id(memberId);
    }

    public void deleteMemberByOwnerId(UUID ownerId) {
        familyMemberRepository.deleteByOwner_Id(ownerId);
    }

    /**
     * Chuyển FamilyMember entity -> DTO
     */
    private FamilyMemberDTO convertToDTO(FamilyMember fm) {
        User member = fm.getMember();

        UserHealthDTO healthDTO = null;
        if (member.getHealthInfo() != null) {
            healthDTO = new UserHealthDTO(
                    member.getHealthInfo().getHeight(),
                    member.getHealthInfo().getWeight(),
                    member.getHealthInfo().getBloodType()
            );
        }

        // ✅ Lấy trực tiếp image (không dùng url)
        String imagePath = null;
        if (member.getAccount() != null) {
            imagePath = member.getAccount().getImage();
        }

        UserSimpleDTO memberDTO = new UserSimpleDTO();
        memberDTO.setId(member.getId());
        memberDTO.setFullName(member.getFullName());
        memberDTO.setUrlImage(imagePath); // 👈 chỉ set image
        memberDTO.setPhone(member.getPhone());
        memberDTO.setEmail(member.getEmail());
        memberDTO.setBirth(member.getBirth());
        memberDTO.setGender(member.getGender());
        memberDTO.setHealthInfo(healthDTO);
        memberDTO.setRoleInFamily("Thành viên");

        FamilyMemberDTO dto = new FamilyMemberDTO();
        dto.setId(fm.getId());
        dto.setRelation(fm.getRelation());
        dto.setOwnerId(fm.getOwner().getId());
        dto.setMember(memberDTO);
        return dto;
    }

    /**
     * Tìm kiếm thành viên trong hộ
     */
    public List<FamilyMemberDTO> searchFamilyMembers(UUID ownerId, String keyword) {
        String normalizedKeyword = normalize(keyword);

        List<FamilyMember> allMembers = familyMemberRepository.findByOwner_Id(ownerId);

        return allMembers.stream()
                .filter(fm -> {
                    String name = fm.getMember().getFullName();
                    if (name == null) return false;
                    return normalize(name).contains(normalizedKeyword);
                })
                .map(this::convertToDTO)
                .toList();
    }

    private String normalize(String input) {
        if (input == null) return "";
        String temp = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{M}+");
        return pattern.matcher(temp).replaceAll("").toLowerCase();
    }

    public boolean isOwner(UUID userId) {
        return familyMemberRepository.existsByOwner_Id(userId);
    }

    public Optional<UUID> findOwnerIdByMemberId(UUID memberId) {
        return familyMemberRepository.findByMember_Id(memberId)
                .map(FamilyMember::getOwner)
                .map(User::getId);
    }
}
