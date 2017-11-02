import java.io.*;
import java.util.LinkedList;

public class DataAnalyzer {
    // A private, internal class used to store a row of data together
    private class DataPoint {
        private int ts;
        private float ax;
        private float ay;
        private float az;
        private float wx;
        private float wy;
        private float wz;
        
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

    // The internal array of DataPoints
    private DataPoint[] dataPoints;
   
    /* The initial number of DataPoint the data structure can hold.
       1500 seemed to be a fairly safe number. */
    private final int INITPOINTS = 1500;
    
    // Current number of data entries
    private int n;

    /* Constants used select which column(s) we'd like to perform the
       problem-set functions on */
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

    /*
     * Double the size of our internal array to account for resizes
     */
    private void resize() {
        DataPoint[] newPoints = new DataPoint[n * 2];
        for (int i = 0; i < n; i++) {
            newPoints[i] = dataPoints[i];
        }
        dataPoints = newPoints;
    }

    /*
     * Adds a DataPoint to the internal array
     */
    private void addDataPoint(DataPoint dataPoint) {
        if (n >= dataPoints.length) resize();
        dataPoints[n++] = dataPoint;
    }
    
    /*
     * Takes raw data, converts it to a datapoint, and adds it to the internal
     * array
     */
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

    /* 
     * Reads a seven column CSV file, and loads it into the data structure
     */
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
     * If there is no such index, returns -1.
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

        for (; indexBegin <= indexEnd; indexBegin++) {
            if (dataPoints[indexBegin].getData(data) > threshold) {
                numContiguous++;
            } else {
                numContiguous = 0;
            }

            if (numContiguous >= winLength) {
                index = indexBegin - winLength + 1;
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
     * If there is no such index, returns -1.
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

        for (; indexBegin >= indexEnd; indexBegin--) {
            if (dataPoints[indexBegin].getData(data) > thresholdLo  &&
                dataPoints[indexBegin].getData(data) < thresholdHi) {
                numContiguous++;
            } else {
                numContiguous = 0;
            }

            if (numContiguous >= winLength) {
                /* Assuming 'first index' refers the first from indexBegin,
                   rather than from the beginning of the sample:
                   index = indexBegin + winLength - 1;
                   Otherwise: */
                index = indexBegin;
                break;
            }
        }

        return index;
    }

    /* 
     * from indexBegin to indexEnd, search data1 for values that are higher than
     * threshold1 and also search data2 for values that are higher than
     * threshold2. Return the first index where both data1 and data2 have values
     * that meet these criteria for at least winLength samples.
     * If there is no such index, returns -1.
     * O(n) worst-case runtime
     */
    public int searchContinuityAboveValueTwoSignals(int data1,
                                                    int data2,
                                                    int indexBegin,
                                                    int indexEnd,
                                                    float threshold1,
                                                    float threshold2,
                                                    int winLength) {
        int index = -1;
        int numContiguous1 = 0;
        int numContiguous2 = 0;

        if (indexBegin >= indexEnd) return -1;
        if (indexBegin < 0 || indexBegin >= n) return -1;
        if (indexEnd < 0 || indexEnd >= n) return -1;

        for (; indexBegin <= indexEnd; indexBegin++) {
            if (dataPoints[indexBegin].getData(data1) > threshold1) {
                numContiguous1++;
            } else {
                numContiguous1 = 0;
            }

            if (dataPoints[indexBegin].getData(data2) > threshold2) {
                numContiguous2++;
            } else {
                numContiguous2 = 0;
            }

            if (numContiguous1 >= winLength && numContiguous2 >= winLength) {
                index = indexBegin - winLength + 1;
                break;
            }
        }

        return index;
    }

    /* 
     * from indexBegin to indexEnd, search data for values that are higher than
     * thresholdLo and lower than thresholdHi. Return the the starting index and
     * ending index of all continuous samples that meet this criteria for at
     * least winLength data points.
     * Returns a LinkedList filled with IndexPairs (Start, End)
     * Returns an empty LinkedList if no indices were found.
     * O(n) runtime in all cases, as we must iterate over the entire array
     * to verify we have found all possible continuous samples.
     */
    public LinkedList<IndexPair> searchMultiContinuityWithinRange(
            int data,
            int indexBegin,
            int indexEnd,
            int thresholdLo,
            int thresholdHi,
            int winLength) {

        int numContiguous = 0;
        IndexPair pair = new IndexPair(-1, -1);
        LinkedList<IndexPair> list = new LinkedList<IndexPair>();

        if (indexBegin >= indexEnd) return null;
        if (indexBegin < 0 || indexBegin >= n) return null;
        if (indexEnd < 0 || indexEnd >= n) return null;

        for (; indexBegin <= indexEnd; indexBegin++) {
            if (dataPoints[indexBegin].getData(data) > thresholdLo  &&
                dataPoints[indexBegin].getData(data) < thresholdHi) {
                if (pair.getStart() == -1) pair.setStart(indexBegin);
                numContiguous++;
            } else {
                /* When we reach the end of a continuous segment that falls in
                   the threshold values, check if it has been winLength data
                   points long. If so, add it to our list of indices */
                if (numContiguous >= winLength) {
                    pair.setEnd(indexBegin - 1);
                    list.add(pair);
                }
                numContiguous = 0;
                pair = new IndexPair(-1, -1);
            }
        }

        /* Edge-case: we finished iterating over the array, and the continuous
           segment didn't end. Check accordingly*/
        if (numContiguous >= winLength) {
            pair.setEnd(indexBegin - 1);
            list.add(pair);
        }

        return list;
    }

    // unit test
    public static void main(String[] args) {
        DataAnalyzer da = new DataAnalyzer(new File("latestSwing.csv"));

        // Should return 1265
        int index = da.searchContinuityAboveValue(DataAnalyzer.TS,
                                                  0,
                                                  1275,
                                                  1579579,
                                                  8);

        // Should return 341
        int index2 = da.backSearchContinuityWithinRange(DataAnalyzer.TS,
                                                        1275,
                                                        0,
                                                        419556,
                                                        432043,
                                                        5);

        // Should return 34
        int index3 = da.searchContinuityAboveValueTwoSignals(DataAnalyzer.AX,
                                                             DataAnalyzer.AY,
                                                             0,
                                                             1275,
                                                             (float) 0.9,
                                                             (float) 0.2,
                                                             5);

        /* Should return [(20,36), (73,286), (337,475), (477,621), (647,743),
           (925,926), (1120, 1264)] */
        LinkedList<IndexPair> indices = da.searchMultiContinuityWithinRange(
                DataAnalyzer.AX,
                0,
                1275,
                0,
                1,
                2);

        System.out.println(index);
        System.out.println(index2);
        System.out.println(index3);
        System.out.println(indices);
    }
}