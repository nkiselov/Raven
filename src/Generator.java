import Attributes.Attribute;
import Attributes.Layer;
import Attributes.PositionAttribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Generator {
    public static final String OBJECT_POSITION_TAG = "object_position";

    private static final Attribute[] attributes = new Attribute[]{
        new PositionAttribute(OBJECT_POSITION_TAG,9)
    };

    public static Layer<Cell> generateRaven(int patternLayerCount, int distractionLayerCount, int options){
        int lr = patternLayerCount + distractionLayerCount;
        if(lr>attributes.length){
            throw new IllegalArgumentException("Requested "+lr+" layers but only "+attributes.length+" are available");
        }
        List<Integer> ind = new ArrayList<>();
        for(int i=0; i< attributes.length; i++){
            ind.add(i);
        }
        Map<String,Layer> patternLayers = new HashMap<>();
        for(int i=0; i<patternLayerCount; i++){
            Attribute at = attributes[ind.remove((int)(Math.random()*ind.size()))];
            Object[] schemes = at.getSchemes();
            patternLayers.put(at.getTag(),at.generateMatrix(schemes[(int)(Math.random()*schemes.length)],options));
        }
        Map<String,Layer> distractionLayers = new HashMap<>();
        for(int i=0; i<distractionLayerCount; i++){
            Attribute at = attributes[ind.remove((int)(Math.random()*ind.size()))];
            distractionLayers.put(at.getTag(),at.generateDistraction(options));
        }
        Cell[][] combinedMatrix = new Cell[3][3];
        for(int i=0; i<3; i++){
            for(int j=0; j<3; j++){
                combinedMatrix[i][j] = new Cell(new HashMap<>());
                for(Map.Entry<String,Layer> pair:patternLayers.entrySet()){
                    combinedMatrix[i][j].attributes.put(pair.getKey(),pair.getValue().matrix[i][j]);
                }
                for(Map.Entry<String,Layer> pair:distractionLayers.entrySet()){
                    combinedMatrix[i][j].attributes.put(pair.getKey(),pair.getValue().matrix[i][j]);
                }
            }
        }
        Cell[] combinedOptions = new Cell[options];
        for(int j=0; j<options; j++) {
            combinedOptions[j] = new Cell(new HashMap<>());
            for(Map.Entry<String,Layer> pair:patternLayers.entrySet()){
                combinedOptions[j].attributes.put(pair.getKey(),pair.getValue().options[j]);
            }
            for(Map.Entry<String,Layer> pair:distractionLayers.entrySet()){
                combinedOptions[j].attributes.put(pair.getKey(),pair.getValue().options[j]);
            }
        }
        return new Layer<>(combinedMatrix,combinedOptions);
    }
}
