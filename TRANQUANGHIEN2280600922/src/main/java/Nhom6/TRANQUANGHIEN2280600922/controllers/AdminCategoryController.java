package Nhom6.TRANQUANGHIEN2280600922.controllers;

import Nhom6.TRANQUANGHIEN2280600922.entities.Category;
import Nhom6.TRANQUANGHIEN2280600922.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/category/list"; 
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("category", new Category());
        return "admin/category/add"; 
    }

    @PostMapping("/add")
    public String add(@ModelAttribute Category category, RedirectAttributes ra) {
        categoryService.addCategory(category);
        ra.addFlashAttribute("success", "Đã thêm thể loại thành công!");
        return "redirect:/admin/categories";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Category category = categoryService.getCategoryById(id).orElse(null);
        if (category == null) return "redirect:/admin/categories";
        model.addAttribute("category", category);
        return "admin/category/edit"; 
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute Category category, RedirectAttributes ra) {
        categoryService.updateCategory(category);
        ra.addFlashAttribute("success", "Đã cập nhật thể loại!");
        return "redirect:/admin/categories";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            categoryService.deleteCategory(id);
            ra.addFlashAttribute("success", "Đã xóa thể loại. Sách liên quan đã chuyển về mục 'Chưa biết'.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/categories";
    }
}