package com.ims.ims.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class OrdersController {

    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("currentPage", "/orders");
        return "orders/orders-list"; 
    }
}
