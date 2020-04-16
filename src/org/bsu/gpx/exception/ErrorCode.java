package org.bsu.gpx.exception;

public enum ErrorCode {

    BASE_INVALID("�������� ������ ����� ��������"), TRACK_INVALID("�������� ������ ����� �����"), BASE_EMPTY("�� ������ ���� ��������"), TRACK_EMPTY(
            "�� ������ ���� �����"), RESULT_EMPTY("�� ������ ���� ����������");

    private String label;

    private ErrorCode(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

}
