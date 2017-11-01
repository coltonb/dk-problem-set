import java.io.*;

public class DataAnalyzer {
    private class DataPoint {
        public int ts;
        public float ax;
        public float ay;
        public float az;
        public float wx;
        public float wy;
        public float wz;
        
        public DataPoint() {
            ts = 0;
            ax = 0;
            ay = 0;
            az = 0;
            wx = 0;
            wy = 0;
            wz = 0;
        }

        public DataPoint(int _ts,
                         float _ax,
                         float _ay,
                         float _az,
                         float _wx,
                         float _wy,
                         float _wz) {
            ts = _ts;
            ax = _ax;
            ay = _ay;
            az = _az;
            wx = _wx;
            wy = _wy;
            wz = _wz;
        }

        public float getData(int column) {
            switch(column) {
                case 0:
                    return (float) ts;
                case 1:
                    return ax;
                case 2:
                    return ay;
                case 3:
                    return az;
                case 4:
                    return wx;
                case 5:
                    return wy;
                case 6:
                    return wz;
                default:
                    return ts;
            }
        }

        public String toString() {
            String str = ts + ", " + ax + ", " + ay + ", " + az + ", " + wx +
                         ", " + wy + ", " + wz;
            return str;
        }
    }

    private DataPoint[] dataPoints;
    private final int INITPOINTS = 1500;
    private int n;

    public static final int TS = 0;
    public static final int AX = 1;
    public static final int AY = 2;
    public static final int AZ = 3;
    public static final int WX = 4;
    public static final int WY = 5;
    public static final int WZ = 6;

    public DataAnalyzer() {
        dataPoints = new DataPoint[INITPOINTS];
        n = 0;
    }

    public DataAnalyzer(File dataFile) {
        dataPoints = new DataPoint[INITPOINTS];
        n = 0;
        readFile(dataFile);
    }

    private void resize() {
        DataPoint[] newPoints = new DataPoint[n * 2];
        for (int i = 0; i < n; i++) {
            newPoints[i] = dataPoints[i];
        }
        dataPoints = newPoints;
    }

    // Takes a datapoint 
    private void addDataPoint(DataPoint dataPoint) {
        if (n >= dataPoints.length) resize();
        dataPoints[n++] = dataPoint;
    }
    
    // Takes raw data, converts it to a datapoint, and adds it to the internal
    // array
    public void addData(int ts,
                        float ax,
                        float ay,
                        float az,
                        float wx,
                        float wy,
                        float wz) {
        DataPoint newPoint = new DataPoint(ts, ax, ay, az, wx, wy, wz);
        addDataPoint(newPoint);
    }

    private boolean readFile(File dataFile) {
        String line;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                int ts = Integer.parseInt(data[0]);
                float ax = Float.parseFloat(data[1]);
                float ay = Float.parseFloat(data[2]);
                float az = Float.parseFloat(data[3]);
                float wx = Float.parseFloat(data[4]);
                float wy = Float.parseFloat(data[5]);
                float wz = Float.parseFloat(data[6]);

                addData(ts, ax, ay, az, wx, wy, wz);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public String stringifyIndex(int index) {
        if (index < 0 || index >= n) return null;
        return dataPoints[index].toString();
    }

    /* Problem-Set Functions */

    /*
     * from indexBegin to indexEnd, search data for values that are higher than
     * threshold. Return the first index where data has values that meet this
     * criteria for at least winLength samples.
     * If there is no such index, return -1.
     * O(n) worst-case runtime
     */
    public int searchContinuityAboveValue(int data,
                                          int indexBegin,
                                          int indexEnd,
                                          float threshold,
                                          int winLength) {
        int index = -1;
        int numContiguous = 0;

        if (indexBegin >= indexEnd) return -1;
        if (indexBegin < 0 || indexBegin >= n) return -1;
        if (indexEnd < 0 || indexEnd >= n) return -1;

        for (; indexBegin < indexEnd; indexBegin++) {
            if (dataPoints[indexBegin].getData(data) > threshold) {
                numContiguous++;
            } else {
                numContiguous = 0;
            }

            if (numContiguous >= winLength) {
                index = indexBegin - numContiguous + 1;
                break;
            }
        }

        return index;
    }

    /* 
     * from indexBegin to indexEnd (where indexBegin is larger than indexEnd),
     * search data for values that are higher than thresholdLo and lower than
     * thresholdHi. Return the first index where data has values that meet this 
     * criteria for at least winLength samples.
     * If there is no such index, return -1.
     * O(n) worst-case runtime
     */
    public int backSearchContinuityWithinRange(int data,
                                               int indexBegin,
                                               int indexEnd,
                                               float thresholdLo,
                                               float thresholdHi,
                                               int winLength) {
        int index = -1;
        int numContiguous = 0;

        if (indexBegin <= indexEnd) return -1;
        if (indexBegin < 0 || indexBegin >= n) return -1;
        if (indexEnd < 0 || indexEnd >= n) return -1;

        for (; indexBegin > indexEnd; indexBegin--) {
            if (dataPoints[indexBegin].getData(data) > thresholdLo  &&
                dataPoints[indexBegin].getData(data) < thresholdHi) {
                numContiguous++;
            } else {
                numContiguous = 0;
            }

            if (numContiguous >= winLength) {
                /*
                 * Assuming 'first index' refers the first from indexBegin,
                 * rather than from the beginning of the sample:
                 * index = indexBegin + numContiguous - 1;
                 * Otherwise:
                 */
                index = indexBegin;
                break;
            }
        }

        return index;
    }
}