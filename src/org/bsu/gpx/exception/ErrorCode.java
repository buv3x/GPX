package org.bsu.gpx.exception;

public enum ErrorCode {

    BASE_INVALID("Неверный формат файла поправок"), TRACK_INVALID("Неверный формат файла трека"), BASE_EMPTY("Не выбран файл поправок"), TRACK_EMPTY(
            "Не выбран файл трека"), RESULT_EMPTY("Не выбран файл результата");

    private String label;

    private ErrorCode(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

}
