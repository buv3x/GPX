package org.bsu.gpx;

import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class Test {

    /**
     * @param args
     * @throws JAXBException
     */
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(GpxType.class);

        Unmarshaller unmarshaller = jc.createUnmarshaller();
        GpxType gpxType = ((JAXBElement<GpxType>) unmarshaller.unmarshal(new File("D:\\Orienteering\\GPX\\Evening_Run (1).gpx"))).getValue();

        BigDecimal latStart = new BigDecimal(54.103932);
        BigDecimal lonStart = new BigDecimal(27.899290);
        
        BigDecimal latOStart = gpxType.getTrk().get(0).getTrkseg().get(0).getTrkpt().get(0).getLat();
        BigDecimal lonOStart = gpxType.getTrk().get(0).getTrkseg().get(0).getTrkpt().get(0).getLon();
        
        BigDecimal latDiff = latStart.subtract(latOStart);
        BigDecimal lonDiff = lonStart.subtract(lonOStart);
        
        for (TrkType trkType : gpxType.getTrk()) {
			for (TrksegType trksegType : trkType.getTrkseg()) {
				for (WptType trkpt : trksegType.getTrkpt()) {
					trkpt.setLat(trkpt.getLat().add(latDiff));
					trkpt.setLon(trkpt.getLon().add(lonDiff));
				}
			}
		}
        
        BigDecimal angle = new BigDecimal(- Math.PI * 2.7 / 180);
        BigDecimal cos = new BigDecimal(Math.cos(angle.doubleValue()));
        BigDecimal sin = new BigDecimal(Math.sin(angle.doubleValue()));
        
        BigDecimal scale = new BigDecimal(0.95);
        
        for (TrkType trkType : gpxType.getTrk()) {
			for (TrksegType trksegType : trkType.getTrkseg()) {
				for (WptType trkpt : trksegType.getTrkpt()) {
					BigDecimal latDelta = trkpt.getLat().subtract(latStart);
			        BigDecimal lonDelta = trkpt.getLon().subtract(lonStart);
			        
			        BigDecimal y = lonDelta.multiply(sin).add(latDelta.multiply(cos));
			        BigDecimal x = lonDelta.multiply(cos).subtract(latDelta.multiply(sin));
			        
			        
			        y = y.multiply(scale);
			        x = x.multiply(scale);
			        
			        trkpt.setLat(y.add(latStart));
			        trkpt.setLon(x.add(lonStart));
			        
				}
			}
		}
        
        Marshaller jaxbMarshaller = jc.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        ObjectFactory objectFactory = new ObjectFactory();
        JAXBElement<GpxType> gpx = objectFactory.createGpx(gpxType);
        jaxbMarshaller.marshal(gpx, new File("D:\\Orienteering\\GPX\\Evening_Run (Converted).gpx"));
        
        System.out.println(gpxType.getWpt().size());

    }

}
