package org.tung.healthycheck.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.tung.healthycheck.model.FamilyMember;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FamilyMemberRepository extends JpaRepository<FamilyMember, UUID> {
    List<FamilyMember> findByOwner_Id(UUID ownerId);
    boolean existsByOwner_IdAndMember_Id(UUID ownerId, UUID memberId);
    void deleteByMember_Id(UUID memberId);
    void deleteByOwner_Id(UUID ownerId);
    boolean existsByOwner_Id(UUID ownerId);
    Optional<FamilyMember> findByMember_Id(UUID memberId);
}
