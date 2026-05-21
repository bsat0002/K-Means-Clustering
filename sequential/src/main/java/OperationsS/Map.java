package OperationsS;

import org.jxmapviewer.JXMapKit;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Random;

public class Map {

    private static int WIDTH = 800;
    private static int HEIGHT = 600;

    public static JFrame createmap(List<Clusters> clusters) {

        JFrame frame = new JFrame("Map of clusters for accumulation sites");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);

        JXMapKit mapKit = new JXMapKit();

        // Setting tile factory
        OSMTileFactoryInfo info = new OSMTileFactoryInfo(
                "Carto",
                "https://a.basemaps.cartocdn.com/light_all"
        );

        DefaultTileFactory factory = new DefaultTileFactory(info);
        mapKit.setTileFactory(factory);

        // map display settings
        mapKit.setMiniMapVisible(false);
        mapKit.setZoomSliderVisible(true);
        mapKit.setAddressLocationShown(false);
        mapKit.setAddressLocation(new GeoPosition(41.8967, 12.4822));
        mapKit.setZoom(15);

        frame.add(mapKit, BorderLayout.CENTER);

        Random rand = new Random();

        // Give each cluster a colour.
        Color[] clusterColours = new Color[clusters.size()];

        for (int i = 0; i < clusters.size(); i++) {
            clusterColours[i] = new Color(
                    rand.nextInt(200) + 55,
                    rand.nextInt(200) + 55,
                    rand.nextInt(200) + 55
            );
        }

        // Custom painter used to draw cluster points and centres on the map.
        mapKit.getMainMap().setOverlayPainter((g, map, width, height) -> {

            Graphics2D g2d = (Graphics2D) g;

            g2d.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            Rectangle viewportBounds = map.getViewportBounds();

            // Draw all accumulation sites.
            for (int i = 0; i < clusters.size(); i++) {

                Clusters cluster = clusters.get(i);
                g2d.setColor(clusterColours[i]);

                for (Records record : cluster.getRecords()) {

                    GeoPosition position = new GeoPosition(
                            record.getLatitude(),
                            record.getLongitude()
                    );

                    Point2D point = map.getTileFactory().geoToPixel(
                            position,
                            map.getZoom()
                    );

                    int x = (int) point.getX() - viewportBounds.x;
                    int y = (int) point.getY() - viewportBounds.y;

                    int size = 4;
                    g2d.fillOval(x - size / 2, y - size / 2, size, size);
                }
            }

            // Draw cluster centres on top.
            for (Clusters cluster : clusters) {

                GeoPosition centrePosition = new GeoPosition(
                        cluster.getCentre_latitude(),
                        cluster.getCentre_longitude()
                );

                Point2D point = map.getTileFactory().geoToPixel(
                        centrePosition,
                        map.getZoom()
                );

                int x = (int) point.getX() - viewportBounds.x;
                int y = (int) point.getY() - viewportBounds.y;

                int size = 14;

                // Draw a white outline around each cluster centre.
                g2d.setColor(Color.WHITE);
                g2d.fillOval(x - size / 2 - 2, y - size / 2 - 2, size + 4, size + 4);

                // Draw the cluster centre as a larger black circle.
                g2d.setColor(Color.BLACK);
                g2d.fillOval(x - size / 2, y - size / 2, size, size);
            }
        });

        // Zooming with mouse wheel.
        mapKit.getMainMap().addMouseWheelListener(
                new org.jxmapviewer.input.ZoomMouseWheelListenerCenter(mapKit.getMainMap())
        );


        // Moving map with clicking.
        org.jxmapviewer.input.PanMouseInputListener pan =
                new org.jxmapviewer.input.PanMouseInputListener(mapKit.getMainMap());

        mapKit.getMainMap().addMouseListener(pan);
        mapKit.getMainMap().addMouseMotionListener(pan);

        mapKit.getMainMap().repaint();

        return frame;
    }
}