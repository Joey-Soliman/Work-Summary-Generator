package com.zoom.restapi.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ZoomTokensRepository extends JpaRepository<ZoomTokens, String> {

    Optional<ZoomTokens> findById(String id);
}
