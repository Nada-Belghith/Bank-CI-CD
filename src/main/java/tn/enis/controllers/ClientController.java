package tn.enis.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tn.enis.entity.Client;
import tn.enis.service.ClientService;

import java.util.List;

@Controller
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/all")
    public String findAll(Model model) {
        model.addAttribute("clients", clientService.findAll());
        return "clients";
    }

    @GetMapping("/all-json")
    @ResponseBody
    public List<Client> findAllJson() {
        return clientService.findAll();
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("client", new Client());
        return "add-client";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Client client) {
        clientService.saveOrUpdate(client);
        return "redirect:/clients/all";
    }

    @GetMapping("/edit/{cin}")
    public String edit(@PathVariable String cin, Model model) {
        model.addAttribute("client", clientService.findById(cin));
        return "edit-client";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Client client) {
        clientService.saveOrUpdate(client);
        return "redirect:/clients/all";
    }

    @PostMapping("/delete")
    @ResponseBody
    public void delete(@RequestParam String cin) {
        clientService.deleteById(cin);
    }

}