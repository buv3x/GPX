package org.bsu.gpx.calculator;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.bsu.gpx.GpxType;
import org.bsu.gpx.ObjectFactory;
import org.bsu.gpx.TrkType;
import org.bsu.gpx.TrksegType;
import org.bsu.gpx.WptType;
import org.bsu.gpx.exception.ErrorCode;
import org.bsu.gpx.exception.GPXException;

public class GPXCalculator {

    @SuppressWarnings("unchecked")
    public void calculate(File fileBase, File fileTrack, File fileResult, CalculationParams params) throws JAXBException, GPXException {
        validate(fileBase, fileTrack, fileResult);

        JAXBContext jc = JAXBContext.newInstance(GpxType.class);

        Unmarshaller unmarshaller = jc.createUnmarshaller();
        GpxType baseGPX = null;
        GpxType trackGPX = null;
        try {
            baseGPX = ((JAXBElement<GpxType>) unmarshaller.unmarshal(fileBase)).getValue();
        } catch (JAXBException e) {
            throw new GPXException(ErrorCode.BASE_INVALID);
        }
        try {
            trackGPX = ((JAXBElement<GpxType>) unmarshaller.unmarshal(fileTrack)).getValue();
        } catch (JAXBException e) {
            throw new GPXException(ErrorCode.TRACK_INVALID);
        }

        Map<Date, Point> basePointsMap = new TreeMap<Date, GPXCalculator.Point>();
        Point mid = fillBasePointsMap(params, baseGPX, basePointsMap);

        if (basePointsMap.size() > 0) {
        	List<WptType> wptTypes = new ArrayList<WptType>();
//            for (WptType wpt : trackGPX.getWpt()) {
//                recalculateWpt(params, basePointsMap, mid, wpt);
//            }
        	int number = 0;
            for (TrkType trk : trackGPX.getTrk()) {
                for (TrksegType trkSeg : trk.getTrkseg()) {
                    for (WptType wpt : trkSeg.getTrkpt()) {
                    	if(number++ % params.getFilter() == 0) {
                    		recalculateWpt(params, basePointsMap, mid, wpt, wptTypes);
                    	}
                    }
                }
            }
            trackGPX.getTrk().clear();
            trackGPX.getWpt().clear();
            trackGPX.getWpt().addAll(wptTypes);
        }

        Marshaller jaxbMarshaller = jc.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        ObjectFactory objectFactory = new ObjectFactory();
        JAXBElement<GpxType> gpx = objectFactory.createGpx(trackGPX);
        jaxbMarshaller.marshal(gpx, fileResult);
    }

    private void validate(File fileBase, File fileTrack, File fileResult) throws GPXException {
        if (fileBase == null) {
            throw new GPXException(ErrorCode.BASE_EMPTY);
        }
        if (fileTrack == null) {
            throw new GPXException(ErrorCode.TRACK_EMPTY);
        }
        if (fileResult == null) {
            throw new GPXException(ErrorCode.RESULT_EMPTY);
        }
    }

    private void recalculateWpt(CalculationParams params, Map<Date, Point> basePointsMap, Point mid, WptType wpt, List<WptType> wptTypes) {
        if (wpt.getTime() != null) {
            Date time = wpt.getTime().toGregorianCalendar().getTime();
            Point basePoint = findBasePoint(basePointsMap, time);
            if (basePoint != null) {
                if (params.isXYUsed()) {
                    wpt.setLat(wpt.getLat().subtract(basePoint.x).add(mid.x));
                    wpt.setLon(wpt.getLon().subtract(basePoint.y).add(mid.y));
                }
                if (params.isZUsed()) {
                    if (wpt.getEle() != null && basePoint.z != null) {
                        wpt.setEle(wpt.getEle().subtract(basePoint.z).add(mid.z));
                        wpt.setName(wpt.getEle().toPlainString());
                    }
                }
            }
            wptTypes.add(wpt);
        }
    }

    private Point findBasePoint(Map<Date, Point> basePointsMap, Date time) {
        Point basePoint = null;

        Date prev = null;
        Date baseTime = null;
        for (Entry<Date, Point> entry : basePointsMap.entrySet()) {
            Date curr = entry.getKey();
            if (curr.compareTo(time) >= 0) {
                if (prev == null) {
                    baseTime = curr;
                } else {
                    long prevDiff = time.getTime() - prev.getTime();
                    long currDiff = curr.getTime() - time.getTime();
                    if (prevDiff > currDiff) {
                        baseTime = curr;
                    } else {
                        baseTime = prev;
                    }
                }
                break;
            }
            prev = curr;
        }

        if (baseTime == null) {
            baseTime = prev;
        }
        if (basePointsMap.containsKey(baseTime)) {
            basePoint = basePointsMap.get(baseTime);
        }

        return basePoint;
    }

    private Point fillBasePointsMap(CalculationParams params, GpxType baseGPX, Map<Date, Point> basePointsMap) {
        Point summ = new Point();
        summ.z = null;
        int count = 0;
        for (TrkType trk : baseGPX.getTrk()) {
            for (TrksegType trkSeg : trk.getTrkseg()) {
                baseGPX.getWpt().addAll(trkSeg.getTrkpt());
            }
        }
        for (WptType wpt : baseGPX.getWpt()) {
            if (wpt.getTime() != null) {
                if (params.isZUsed()) {
                    if (summ.z == null && wpt.getEle() != null) {
                        summ.z = wpt.getEle();
                    }
                }

                if (params.isXYUsed()) {
                    summ.x = summ.x.add(wpt.getLat());
                    summ.y = summ.y.add(wpt.getLon());
                    count++;
                }

                basePointsMap.put(wpt.getTime().toGregorianCalendar().getTime(), new Point(wpt.getLat(), wpt.getLon(), wpt.getEle()));
            }
        }

        if (params.isXYUsed()) {
            summ.x = summ.x.divide(BigDecimal.valueOf(count), RoundingMode.HALF_UP);
            summ.y = summ.y.divide(BigDecimal.valueOf(count), RoundingMode.HALF_UP);
        }

        return summ;
    }

    private class Point {
        private BigDecimal x = BigDecimal.ZERO;
        private BigDecimal y = BigDecimal.ZERO;
        private BigDecimal z = BigDecimal.ZERO;

        public Point() {

        }

        public Point(BigDecimal lat, BigDecimal lon, BigDecimal ele) {
            x = lat;
            y = lon;
            z = ele;
        }
    }

}
