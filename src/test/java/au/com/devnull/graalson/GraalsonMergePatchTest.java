package au.com.devnull.graalson;

import jakarta.json.Json;
import jakarta.json.JsonMergePatch;
import jakarta.json.JsonObject;
import org.json.JSONException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 *
 * @author wozza
 */
public class GraalsonMergePatchTest {

    @Test
    @Disabled
    public void testMergeDiff() throws JSONException {
        String orig = "merge_orig.json";
        String patch = "merge_patch.json";
        String result = "merge_result.json";

        JsonObject jOrig = Json.createReader(ClassLoader.getSystemResourceAsStream(orig)).readObject();
        JsonObject jPatch = Json.createReader(ClassLoader.getSystemResourceAsStream(patch)).readObject();
        JsonObject jResult = Json.createReader(ClassLoader.getSystemResourceAsStream(result)).readObject();
        JsonMergePatch mPatch = Json.createMergePatch(jPatch);
        JsonMergePatch mDiff = Json.createMergeDiff(jOrig, jResult);
        JSONAssert.assertEquals(mPatch.toString(), mDiff.toString(), JSONCompareMode.STRICT_ORDER);
    }

    @Test
    @Disabled
    public void testMergePatchApply() {
        //TODO:
    }

}
