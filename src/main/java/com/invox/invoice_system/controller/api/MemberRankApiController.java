package com.invox.invoice_system.controller.api;

import com.invox.invoice_system.dto.MemberRankDTO;
import com.invox.invoice_system.service.MemberRankService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/member-ranks")
@RequiredArgsConstructor
public class MemberRankApiController {

    private final MemberRankService memberRankService;

    @GetMapping
    public ResponseEntity<List<MemberRankDTO>> getAllMemberRanks() {
        List<MemberRankDTO> memberRanks = memberRankService.getAllMemberRanks();
        return new ResponseEntity<>(memberRanks, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberRankDTO> getMemberRankById(@PathVariable Long id) {
        return memberRankService.getMemberRankById(id)
                .map(rank -> new ResponseEntity<>(rank, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<?> createMemberRank(@RequestBody MemberRankDTO memberRankDTO) {
        MemberRankDTO createdRank = memberRankService.createMemberRank(memberRankDTO);
        return new ResponseEntity<>(createdRank, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMemberRank(@PathVariable Long id, @RequestBody MemberRankDTO memberRankDTO) {
        MemberRankDTO updatedRank = memberRankService.updateMemberRank(id, memberRankDTO);
        return new ResponseEntity<>(updatedRank, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMemberRank(@PathVariable Long id) {
        memberRankService.deleteMemberRank(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}