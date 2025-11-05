package org.tung.healthycheck.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.tung.healthycheck.dto.FamilyMemberDTO;
import org.tung.healthycheck.model.Account;
import org.tung.healthycheck.model.FamilyMember;
import org.tung.healthycheck.model.User;
import org.tung.healthycheck.services.AccountService;
import org.tung.healthycheck.services.FamilyMemberService;
import org.tung.healthycheck.services.UserService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/family-members")
public class FamilyMemberController {

    @Autowired
    private FamilyMemberService familyMemberService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public ResponseEntity<List<FamilyMemberDTO>> getFamilyMembers() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Account account = accountService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản cho username: " + username));

        User currentUser = userService.getUserByAccountId(account.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng cho account id: " + account.getId()));

        // 🔹 Kiểm tra người này có phải chủ hộ không
        boolean isOwner = familyMemberService.isOwner(currentUser.getId());

        UUID ownerId;
        if (isOwner) {
            ownerId = currentUser.getId();
        } else {
            // 🔹 Nếu không phải chủ hộ -> tìm chủ hộ mà người này thuộc về
            ownerId = familyMemberService.findOwnerIdByMemberId(currentUser.getId())
                    .orElseThrow(() -> new RuntimeException("Người này chưa thuộc hộ nào"));
        }

        return ResponseEntity.ok(familyMemberService.getFamilyMembers(ownerId));
    }
    @PostMapping("/add-by-phone")
    public ResponseEntity<?> addMemberByPhone(@RequestBody Map<String, String> body) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        User owner = userService.getUserByAccountId(account.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        // 🔹 Kiểm tra quyền
        if (!familyMemberService.isOwner(owner.getId())) {
            return ResponseEntity.status(403).body(Map.of("error", "Bạn không có quyền thêm thành viên"));
        }

        try {
            String phone = body.get("phone");
            String relation = body.get("relation");
            String message = familyMemberService.addMemberByPhone(owner.getId(), phone, relation);
            return ResponseEntity.ok(Map.of("message", message));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMember(@PathVariable UUID id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        User currentUser = userService.getUserByAccountId(account.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        // 🔹 Chỉ chủ hộ mới được xóa
        if (!familyMemberService.isOwner(currentUser.getId())) {
            return ResponseEntity.status(403).body(Map.of("error", "Bạn không có quyền xóa thành viên"));
        }

        familyMemberService.deleteMemberByUserId(id);
        return ResponseEntity.ok(Map.of("message", "Xóa thành viên thành công"));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchFamilyMembers(
            @RequestBody String keyword) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Account account = accountService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản cho username: " + username));
        User owner = userService.getUserByAccountId(account.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng cho account id: " + account.getId()));
        try {
            List<FamilyMemberDTO> results = familyMemberService.searchFamilyMembers(owner.getId(), keyword);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/is-owner")
    public ResponseEntity<Map<String, Boolean>> checkIsOwner() {
        // Lấy username của người đang đăng nhập
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Account account = accountService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản cho username: " + username));

        User currentUser = userService.getUserByAccountId(account.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng cho account id: " + account.getId()));

        // Kiểm tra xem người này có phải là chủ hộ không
        boolean isOwner = familyMemberService.isOwner(currentUser.getId());

        return ResponseEntity.ok(Map.of("isOwner", isOwner));
    }

}
