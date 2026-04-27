package com.restaurants.auth.service;

import com.restaurants.auth.dto.SignUpRequestDTO;
import com.restaurants.auth.entity.UtilisateurEntity;

public interface UtilisateurService {
    UtilisateurEntity creerUtilisateur(SignUpRequestDTO dto);
}
