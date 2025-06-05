package com.invox.invoice_system.controller.page;

import com.invox.invoice_system.dto.MemberRankDTO;
import com.invox.invoice_system.service.MemberRankService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/member-ranks")
@RequiredArgsConstructor
public class MemberRankPageController {

    private final MemberRankService memberRankService;

    @GetMapping
    public String listMemberRanks(Model model) {
        List<MemberRankDTO> memberRanks = memberRankService.getAllMemberRanks();
        model.addAttribute("memberRanks", memberRanks);
        return "memberranks/list-memberranks";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("memberRank", new MemberRankDTO());
        return "memberranks/create-memberrank";
    }

    @PostMapping("/new")
    public String createMemberRank(@ModelAttribute("memberRank") MemberRankDTO memberRankDTO,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        try {
            memberRankService.createMemberRank(memberRankDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Hạng thành viên đã được tạo thành công!");
            return "redirect:/member-ranks";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("memberRank", memberRankDTO);
            return "memberranks/create-memberrank";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<MemberRankDTO> memberRank = memberRankService.getMemberRankById(id);
        if (memberRank.isPresent()) {
            model.addAttribute("memberRank", memberRank.get());
            return "memberranks/create-memberrank";
        }
        return "redirect:/member-ranks";
    }

    @PostMapping("/edit/{id}")
    public String updateMemberRank(@PathVariable Long id,
                                   @ModelAttribute("memberRank") MemberRankDTO memberRankDTO,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        try {
            memberRankService.updateMemberRank(id, memberRankDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Hạng thành viên đã được cập nhật thành công!");
            return "redirect:/member-ranks";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("memberRank", memberRankDTO);
            return "memberranks/create-memberrank";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteMemberRank(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            memberRankService.deleteMemberRank(id);
            redirectAttributes.addFlashAttribute("successMessage", "Hạng thành viên đã được xóa thành công!");
            return "redirect:/member-ranks";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/member-ranks";
        }
    }
}