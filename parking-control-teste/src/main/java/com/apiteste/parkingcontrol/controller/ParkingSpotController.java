package com.apiteste.parkingcontrol.controller;

import com.apiteste.parkingcontrol.dto.ParkingSpotDto;
import com.apiteste.parkingcontrol.model.ParkingSpotModel;
import com.apiteste.parkingcontrol.service.ParkingSpotService;
import org.apache.coyote.Response;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
// Permite acesso de qualquer fonte
@CrossOrigin(origins = "*", maxAge = 3600)
// Defina a uri a nível de classe
@RequestMapping("/parking-spot")
public class ParkingSpotController {
    //    Criamos o ponto de injeção para chamar o ParkingSpotService criando uma variável final e utilizando o construtor para atribuí-la
    final ParkingSpotService parkingSpotService;

    public ParkingSpotController(ParkingSpotService parkingSpotService){
        this.parkingSpotService = parkingSpotService;
    }

    @PostMapping
//    Utilizamos o tipo Object porque teremos diferentes tipos de retorno
    public ResponseEntity<Object> saveParkingSpot(@RequestBody @Valid ParkingSpotDto parkingSpotDto){
        if(parkingSpotService.existsByLicensePlateCar(parkingSpotDto.getLicensePlateCar())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: License Plate Car is already in use.");
        }
        if(parkingSpotService.existsByParkingSpotNumber(parkingSpotDto.getParkingSpotNumber())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking Spot Number is already in use.");
        }
        if(parkingSpotService.existsByApartmentAndBlock(parkingSpotDto.getApartment(), parkingSpotDto.getBlock())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Apartment and block already in use");
        }

        var parkingSpotModel = new ParkingSpotModel();
//        C  onvertendo os atributos do DTO para o Model
        BeanUtils.copyProperties(parkingSpotDto, parkingSpotModel);
//        Atribuindo data e hora automaticamente
        parkingSpotModel.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));
//        Retorna o Status sinalizando a criação e salva o DTO no parkingSpotModel
        return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpotService.save(parkingSpotModel));
    }
    @GetMapping
    public ResponseEntity<List<ParkingSpotModel>> getAllParkingSpots(){
        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.findAll());
    }

    @GetMapping("/{id}")
//    Utilizando classe Object porque se não houver ParkingSpotModel para o ID solicitado, teremos de retornar outra classe, o que impossibilita utilizar a classe ParkingSpotModel
//    A anotação path varable indica que deverá seguir um caminho determinado pelo sou parametro, que deve ser o mesmo passado dentro das chaves no @GetMapping
    public ResponseEntity<Object> getOneParkingSpot(@PathVariable(value = "id")UUID id){
        Optional <ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
        if(!parkingSpotModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ID not found.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotModelOptional.get());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteParkingSpot(@PathVariable(value = "id")UUID id){
        Optional <ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
        if(!parkingSpotModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id not found");
        }
        parkingSpotService.delete(parkingSpotModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body("Parking spot deleted successfully");
    }

    @PutMapping("/{id}")
//    ID do model a ser atualizado e DTO com os dados a atualizar
    public ResponseEntity<Object> updateParkingSpot(@PathVariable(value = "id")UUID id,
                                                    @RequestBody @Valid ParkingSpotDto parkingSpotDto){
        Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
        if(!parkingSpotModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found");
        }
//        Convertendo DTO para model
//        Criando uma instancia de ParkingSpotModel utilizando o Optional buscado no banco de dados
        var  parkingSpotModel = parkingSpotModelOptional.get();
//        Devemos settar todos os campos que poderão ser atualizados (todos, menos ID e data que atualiza automaticamente)
        parkingSpotModel.setParkingSpotNumber(parkingSpotDto.getParkingSpotNumber());
        parkingSpotModel.setApartment(parkingSpotDto.getApartment());
        parkingSpotModel.setBlock(parkingSpotDto.getBlock());
        parkingSpotModel.setModelCar(parkingSpotDto.getModelCar());
        parkingSpotModel.setBrandCar(parkingSpotDto.getBrandCar());
        parkingSpotModel.setColorCar(parkingSpotDto.getColorCar());
        parkingSpotModel.setLicensePlateCar(parkingSpotDto.getLicensePlateCar());
        parkingSpotModel.setResponsibleName(parkingSpotDto.getResponsibleName());

        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.save(parkingSpotModel));
    }

}
