package org.dec.springlab.controller;

import org.dec.springlab.repository.CashCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/cashcards")
public class CashCardController {
    private final CashCardRepository cashCardRepository;

    @Autowired
    private CashCardController(CashCardRepository cashCardRepository){
        this.cashCardRepository = cashCardRepository;
    }

    @GetMapping
    private ResponseEntity<List<CashCard>> findAll(Pageable pageable, Principal principal) {
        Page<CashCard> page = cashCardRepository.findByOwner(
                principal.getName(),
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
                ));
        return ResponseEntity.ok(page.getContent());
    }

    @GetMapping("/{cashcardId}")
    private ResponseEntity<CashCard> findCashCardById(@PathVariable Long cashcardId, Principal principal){
        CashCard cashCard =  findCashCard(cashcardId, principal);
        if(cashCard!=null){
            return ResponseEntity.ok(cashCard);
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    private ResponseEntity<Void> createCashCard(@RequestBody CashCard cashCard, UriComponentsBuilder ucb, Principal principal){
        CashCard newCashCard = new CashCard(null, cashCard.amount(), principal.getName());
       CashCard savedCashCard = cashCardRepository.save(newCashCard);
       URI locationOfNewCashCard = ucb.path("cashcards/{id}").buildAndExpand(savedCashCard.id()).toUri();
       return ResponseEntity.created(locationOfNewCashCard).build();
    }

    @PutMapping("/{cashCardId}")
    private ResponseEntity<Void> updateCashCard(@PathVariable Long cashCardId, @RequestBody CashCard cashCardUpdate, UriComponentsBuilder ucb, Principal principal){
        CashCard cashCard = findCashCard(cashCardId, principal);
        if (cashCard != null) {
            CashCard updatedCashCard = new CashCard(cashCard.id(), cashCardUpdate.amount(), principal.getName());
            cashCardRepository.save(updatedCashCard);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private CashCard findCashCard(Long requestedId, Principal principal) {
        return cashCardRepository.findByIdAndOwner(requestedId, principal.getName());
    }

    @DeleteMapping("/{cashcardId}")
    private ResponseEntity<Void> deleteCashCard(@PathVariable Long cashcardId, Principal principal) {
        if (cashCardRepository.existsByIdAndOwner(cashcardId, principal.getName())) {
            cashCardRepository.deleteById(cashcardId);
            return ResponseEntity.noContent().build();
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }

}
