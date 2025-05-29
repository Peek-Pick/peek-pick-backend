package org.beep.sbpp.barcode.service;

public interface BarcodeService {
    void saveHistoryByBarcode(String barcode, Long userId);
}
