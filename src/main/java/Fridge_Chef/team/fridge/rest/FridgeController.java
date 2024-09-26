package Fridge_Chef.team.fridge.rest;

import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.fridge.domain.FridgeIngredient;
import Fridge_Chef.team.fridge.rest.request.FridgeIngredientRequest;
import Fridge_Chef.team.fridge.rest.response.FridgeIngredientResponse;
import Fridge_Chef.team.fridge.rest.response.FridgeResponse;
import Fridge_Chef.team.fridge.service.FridgeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/fridges")
@RequiredArgsConstructor
public class FridgeController {

    private final FridgeService fridgeService;

    //냉장고 생성하기?
    @PostMapping("/")
    public ResponseEntity<?> create(HttpServletRequest request) throws ApiException {

        try {
            //request에서 access token 추출
            //access token에서 user id 클레임 추출

            UUID userId = UUID.randomUUID();
            fridgeService.createFridge(userId);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (ApiException e) {
            throw e;
        }
    }

    //냉장고 조회
    @GetMapping("/")
    public ResponseEntity<?> search(HttpServletRequest request) throws ApiException {

        try {
            //request에서 access token 추출
            //access token에서 user id 클레임 추출

            UUID userId = UUID.randomUUID();
            List<FridgeIngredientResponse> response = fridgeService.getFridge(userId);
            return ResponseEntity.ok().body(response);
        } catch (ApiException e) {
            throw e;
        }
    }

    //냉장고 재료 등록
    @PostMapping("/ingredients")
    public ResponseEntity<?> add(HttpServletRequest request, @RequestBody List<FridgeIngredientRequest> ingredientsRequest) throws ApiException {

        try {
            //access token에서 user id 클레임 추출
            UUID userId = UUID.randomUUID();
            fridgeService.addIngredients(userId, ingredientsRequest);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (ApiException e) {
            throw e;
        }
    }

    //냉장고 재료 삭제
    @DeleteMapping("/ingredients")
    public ResponseEntity<?> delete(HttpServletRequest request, @RequestBody FridgeIngredient ingredientRequest) throws ApiException {

        try {
            //access token에서 user id 클레임 추출
            Long userId = 1L;
            fridgeService.deleteIngredients(userId, ingredientRequest);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (ApiException e) {
            throw e;
        }
    }
}
