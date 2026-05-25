package kr.go.kaptnet.board.controller;

import kr.go.kaptnet.board.dto.BoardDto;
import kr.go.kaptnet.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("message", "Spring boot Board List");
        model.addAttribute("boards", boardService.getAllBoardDtos());
        return "board/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("board", boardService.getBoardDtoById(id));
        return "board/detail";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("board", new BoardDto());
        model.addAttribute("isEdit", false);
        return "board/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("board", boardService.getBoardDtoById(id));
        model.addAttribute("isEdit", true);
        return "board/form";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute BoardDto board, RedirectAttributes redirectAttributes) {
        boardService.createBoardDto(board);
        redirectAttributes.addFlashAttribute("message", "게시글이 작성되었습니다.");
        return "redirect:/board/" + board.getId();
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @ModelAttribute BoardDto board,
                        RedirectAttributes redirectAttributes) {
        board.setId(id);
        boardService.updateBoardDto(board);
        redirectAttributes.addFlashAttribute("message", "게시글이 수정되었습니다.");
        return "redirect:/board/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boardService.deleteBoard(id);
        redirectAttributes.addFlashAttribute("message", "게시글이 삭제되었습니다.");
        return "redirect:/board";
    }
}
