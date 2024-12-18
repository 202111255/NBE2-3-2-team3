package com.example.bestme.dto.response;

import com.example.bestme.domain.Item;

public record ItemDetailResponse(
        Long id,
        String name,
        String imageUrl,
        String purchaseUrl,
        String color,
        String category
) {

    public static ItemDetailResponse from(Item item) {
        return new ItemDetailResponse(
                item.getId(),
                item.getName(),
                item.getImageUrl(),
                item.getPurchaseUrl(),
                item.getColor().getName(),
                item.getCategory().getName()
        );
    }
}