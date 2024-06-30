package me.bilyan.bilyan_hadzhi_employees.home;

import me.bilyan.bilyan_hadzhi_employees.time.DateTimeFormatterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/")
public class HomeController {
    private final DateTimeFormatterService dateTimeFormatterService;

    @Autowired
    public HomeController(DateTimeFormatterService dateTimeFormatterService) {
        this.dateTimeFormatterService = dateTimeFormatterService;
    }

    @GetMapping
    public String home(Model model) {
        var supportedDateFormats = dateTimeFormatterService.getSupportedFormats();
        model.addAttribute("formats", supportedDateFormats);

        return "home";
    }
}
