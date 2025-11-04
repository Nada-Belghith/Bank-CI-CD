package tn.enis.controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import tn.enis.entity.Client;
import tn.enis.entity.Compte;
import tn.enis.service.ClientService;
import tn.enis.service.CompteService;

@Controller
@RequestMapping("/comptes")
@RequiredArgsConstructor
public class CompteController {
	private final CompteService compteService;
	private final ClientService clientService;

	@GetMapping({ "/all", "/index" })
	public String findAll(Model model) {
		model.addAttribute("comptes", compteService.findAll());
		return "comptes";
	}

	@ResponseBody
	@GetMapping("/all-json")
	public List<Compte> findAllJson(Model model) {
		return compteService.findAll();
	}

	@GetMapping("/add")
	public String showAddForm(Model model) {
		model.addAttribute("compte", new Compte());
		model.addAttribute("clients", clientService.findAll());
		return "add-compte";
	}

	@PostMapping("/save")
	public String save(@ModelAttribute Compte compte, @RequestParam(required = false) String clientCin, Model model,
			RedirectAttributes redirectAttributes) {

		if (compte.getSolde() < 0) {
			model.addAttribute("error", "Solde cannot be negative.");
			model.addAttribute("compte", compte);
			model.addAttribute("clients", clientService.findAll());
			return "add-compte";
		}

		try {
			Client client;
			client = clientService.findById(clientCin);

			compte.setClient(client);
			compteService.saveOrUpdate(compte);

			return "redirect:/comptes/all";
		} catch (Exception e) {
			model.addAttribute("error", "Error creating compte: " + e.getMessage());
			model.addAttribute("compte", compte);
			model.addAttribute("clients", clientService.findAll());
			return "add-compte";
		}
	}

	@GetMapping("/edit/{rib}")
    public String edit(@PathVariable Integer rib, Model model) {
        model.addAttribute("compte", compteService.findById(rib));
        model.addAttribute("clients", clientService.findAll());
        return "editCompte";
    }

	 @PostMapping("/delete")
	    @ResponseBody
	    public void delete(@RequestParam Integer rib) {
	        compteService.deleteById(rib);
	        
	    }

	@PostMapping("/update")
	public String update(@ModelAttribute Compte compte, @RequestParam String clientCin, Model model) {
		if (compte.getSolde() < 0) {
			model.addAttribute("error", "Solde cannot be negative.");
			model.addAttribute("compte", compte);
			model.addAttribute("clients", clientService.findAll());
			return "edit-compte";
		}
		try {
			Client client = clientService.findById(clientCin);
			compte.setClient(client);
			compteService.saveOrUpdate(compte);
			return "redirect:/comptes/all";
		} catch (Exception e) {
			model.addAttribute("error", "Invalid client CIN: " + clientCin);
			model.addAttribute("compte", compte);
			model.addAttribute("clients", clientService.findAll());
			return "edit-compte";
		}
	}

	@GetMapping("/search")
	@ResponseBody
	public List<Compte> searchComptes(@RequestParam("cin") String cin) {
		return compteService.findByClientCin(cin);
	}

}
