package com.example.ilyos.controllers;

import com.example.ilyos.dto.PersonDTO;
import com.example.ilyos.models.Person;
import com.example.ilyos.services.PeopleService;
import com.example.ilyos.util.PersonErrorResponse;
import com.example.ilyos.util.PersonNotCreatedException;
import com.example.ilyos.util.PersonNotFoundException;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/people")
public class PeopleController {

    private final PeopleService peopleService;
    private final ModelMapper modelMapper;

    @Autowired
    public PeopleController(PeopleService peopleService, ModelMapper modelMapper) {
        this.peopleService = peopleService;
        this.modelMapper = modelMapper;
    }

    @GetMapping()
    public List<PersonDTO> getAll(){
        return peopleService.findAll().stream().map(this::converToDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public PersonDTO getPerson(@PathVariable("id") int id) throws Exception {
        return converToDto(peopleService.findOne(id)); //Jackson автоматически конвертирует в JSON
    }

    private PersonDTO converToDto(Person person) {
        return modelMapper.map(person, PersonDTO.class);
    }

    @DeleteMapping("/{id}")
    public void deletePerson(@PathVariable("id") int id){
        peopleService.delete(id);
    }

    @PostMapping
    public ResponseEntity<HttpStatus> savePerson(@RequestBody @Valid PersonDTO personDTO, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            StringBuilder errorMsg = new StringBuilder();

            List<FieldError> errors = bindingResult.getFieldErrors();
            for(FieldError error:errors) {
                errorMsg.append(error.getField()).append(" - ")
                        .append(error.getDefaultMessage())
                        .append(";");
            }
            throw new PersonNotCreatedException(errorMsg.toString());
        }

        peopleService.save(converToPerson(personDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotFoundException e){
        PersonErrorResponse response = new PersonErrorResponse("Person with this id wasn't found", System.currentTimeMillis());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotCreatedException e){
        PersonErrorResponse response = new PersonErrorResponse(e.getMessage(), System.currentTimeMillis());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private Person converToPerson(PersonDTO personDTO) {
        return modelMapper.map(personDTO, Person.class);
    }
}
