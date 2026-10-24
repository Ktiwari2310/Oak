package com.oak.response;

import com.oak.entity.Labels;

public record LabelsResponse(
        Integer id,
        Long repositoryId,
        String name,
        String color,
        String description) {

    public static LabelsResponse from(Labels label) {
        return new LabelsResponse(
                label.getId(),
                label.getRepository() == null ? null : label.getRepository().getId(),
                label.getName(),
                label.getColor(),
                label.getDescription());
    }
}
