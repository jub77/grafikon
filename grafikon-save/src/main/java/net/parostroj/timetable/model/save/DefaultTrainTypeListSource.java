package net.parostroj.timetable.model.save;

import net.parostroj.timetable.model.Permissions;
import net.parostroj.timetable.model.ls.LSException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import net.parostroj.timetable.model.TrainType;
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

    public static Pair<TrainsDataDto, List<TrainType>> getDefaultTypeList(Permissions permissions) throws LSException {
        LSTrainTypeSerializer serializer = LSTrainTypeSerializer.getLSTrainTypeSerializer();
        LSTrainTypeList lsList = serializer.load(new InputStreamReader(DefaultTrainTypeListSource.getDefaultTypesInputStream(), StandardCharsets.UTF_8));
        return new Pair<>(lsList.getTrainsData(permissions), lsList.getTrainTypeList());
    }
}
