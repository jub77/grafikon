package net.parostroj.timetable.model.save;

import net.parostroj.timetable.model.ls.LSException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import net.parostroj.timetable.model.TrainType;
import net.parostroj.timetable.model.TrainsData;
import net.parostroj.timetable.utils.Pair;

/**
 * This class loads default train types using locale.
 * 
 * @author jub
 */
public class DefaultTrainTypeListSource {
    public static InputStream getDefaultTypesInputStream() {
        String resourceName = ModelResourceLoader.getString("train.types.file");
        return DefaultTrainTypeListSource.class.getResourceAsStream(resourceName);
    }
    
    public static Pair<TrainsData, List<TrainType>> getDefaultTypeList() throws LSException {
        try {
            LSTrainTypeSerializer serializer = LSTrainTypeSerializer.getLSTrainTypeSerializer(LSSerializer.getLatestVersion());
            LSTrainTypeList lsList = serializer.load(new InputStreamReader(DefaultTrainTypeListSource.getDefaultTypesInputStream(), "utf-8"));
            return new Pair<TrainsData, List<TrainType>>(lsList.getTrainsData(), lsList.getTrainTypeList());
        } catch (UnsupportedEncodingException e) {
            throw new LSException("Cannot load default train type list.", e);
        }
    }
}
