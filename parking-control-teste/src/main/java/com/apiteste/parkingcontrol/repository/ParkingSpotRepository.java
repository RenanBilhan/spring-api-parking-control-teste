package com.apiteste.parkingcontrol.repository;

import com.apiteste.parkingcontrol.model.ParkingSpotModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
// Extendemos JpaRepository com modelo que sera utilizado (ParkingSpotModel) e o identificador(UUID)
public interface ParkingSpotRepository extends JpaRepository<ParkingSpotModel, UUID> {

    public boolean existsByLicensePlateCar(String licensePlateCar);
    public boolean existsByParkingSpotNumber(String parkingSpotNumber);
    public boolean existsByApartmentAndBlock(String apartment, String block);

}
