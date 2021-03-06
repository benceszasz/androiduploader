package com.nightscout.android.barcode;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.nightscout.android.R;
import com.nightscout.android.preferences.AndroidPreferences;
import com.nightscout.android.settings.SettingsActivity;
import com.nightscout.android.test.RobolectricTestBase;
import com.nightscout.core.preferences.NightscoutPreferences;

import net.tribe7.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowPreferenceManager;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class AndroidBarcodeTest extends RobolectricTestBase {
    Activity activity;
    SharedPreferences sharedPrefs;
    String jsonConfig = null;
    NightscoutPreferences prefs;

    @Before
    public void setUp() {
        activity = Robolectric.buildActivity(SettingsActivity.class).create().get();
        sharedPrefs = ShadowPreferenceManager.getDefaultSharedPreferences(getShadowApplication().getApplicationContext());
        prefs = new AndroidPreferences(activity.getApplicationContext(), sharedPrefs);
    }

    private void setValidMongoOnlyWithIntentResult(){
        jsonConfig = "{'mongo':{'uri':'mongodb://user:pass@test.com/cgm_data'}}";
        fakeActivityResult();
    }

    private void setSingleValidApiOnlyWithIntentResult(){
        jsonConfig = "{'rest':{'endpoint':['http://abc@test.com/v1']}}";
        fakeActivityResult();
    }

    private void setValidMqttOnlyWithIntentResult() {
        jsonConfig = "{'mqtt':{'uri':'ssl://test:test@m11.cloudmqtt.com:12345'}}";
        fakeActivityResult();
    }

    private void setMqttNoUserPassOnlyWithIntentResult() {
        jsonConfig = "{'mqtt':{'uri':'ssl://m11.cloudmqtt.com:12345'}}";
        fakeActivityResult();
    }

    private void setSingleValidApiAndMongoWithIntentResult(){
        jsonConfig = "{'mongo':{'uri':'mongodb://user:pass@test.com/cgm_data'}, 'rest':{'endpoint':['http://abc@test.com/']}}";
        fakeActivityResult();
    }

    private void setMultipleValidApiOnlyWithIntentResult(){
        jsonConfig = "{'rest':{'endpoint':['http://abc@test.com/v1', 'http://test.com/']}}";
        fakeActivityResult();
    }

    private void setEmptyValidApiOnlyWithIntentResult(){
        jsonConfig = "{'rest':{'endpoint':[]}}";
        fakeActivityResult();
    }

    private void setEmptyValidMongoOnlyWithIntentResult(){
        jsonConfig = "{'mongo':{}}";
        fakeActivityResult();
    }

    private void setInvalidConfigWithValidJson(){
        jsonConfig = "{'some':{'random':['values']}}";
        fakeActivityResult();
    }

    private void setInvalidJsonWithIntentResult(){
        jsonConfig = "{foo bar";
        fakeActivityResult();
    }

    private void fakeActivityResult(){
        Intent intent = createFakeScanIntent(jsonConfig);
        SettingsActivity activity = Robolectric.buildActivity(SettingsActivity.class)
                .create()
                .start()
                .resume()
                .get();
        activity.onActivityResult(IntentIntegrator.REQUEST_CODE, Activity.RESULT_OK, intent);
    }

    @Test
    public void mqttConfigShouldMqttEnablePrefsOnScanResult() throws Exception {
        setValidMqttOnlyWithIntentResult();
        assertThat(prefs.isMqttEnabled(), is(true));
    }

    @Test
    public void mqttConfigShouldSetMqttUriPrefsOnScanResult() throws Exception {
        setValidMqttOnlyWithIntentResult();
        assertThat(prefs.getMqttEndpoint(), is("ssl://m11.cloudmqtt.com:12345"));
    }

    @Test
    public void mqttConfigShouldSetMqttUserPrefsOnScanResult() throws Exception {
        setValidMqttOnlyWithIntentResult();
        assertThat(prefs.getMqttUser(), is("test"));
    }

    @Test
    public void mqttConfigShouldSetMqttPassPrefsOnScanResult() throws Exception {
        setValidMqttOnlyWithIntentResult();
        assertThat(prefs.getMqttUser(), is("test"));
    }

    @Test
    public void invalidMqttConfigShouldNotEnableMqttOnScanResult() throws Exception {
        setMqttNoUserPassOnlyWithIntentResult();
        assertThat(prefs.isMqttEnabled(), is(false));
    }

    @Test
    public void mongoConfigShouldMongoEnablePrefsOnScanResult() throws Exception {
        setValidMongoOnlyWithIntentResult();
        assertThat(prefs.isMongoUploadEnabled(), is(true));
    }

    @Test
    public void mongoConfigShouldSetMongoUriPrefsOnScanResult() throws Exception {
        setValidMongoOnlyWithIntentResult();
        assertThat(prefs.getMongoClientUri(), is("mongodb://user:pass@test.com/cgm_data"));
    }

    @Test
    public void mongoConfigShouldNotEnableApiPrefsOnScanResult() throws Exception {
        setValidMongoOnlyWithIntentResult();
        assertThat(prefs.isRestApiEnabled(), is(false));
    }

    @Test
    public void mongoConfigShouldNotEnableMqttPrefsOnScanResult() throws Exception {
        setValidMongoOnlyWithIntentResult();
        assertThat(prefs.isMqttEnabled(), is(false));
    }


    @Test
    public void apiConfigShouldEnableApiPrefsOnScanResult() throws Exception{
        setSingleValidApiOnlyWithIntentResult();
        assertThat(prefs.isRestApiEnabled(), is(true));
    }

    @Test
    public void apiConfigShouldSetApiPrefsOnScanResult() throws Exception{
        setSingleValidApiOnlyWithIntentResult();
        List<String> uris = Lists.newArrayList("http://abc@test.com/v1");
        assertThat(prefs.getRestApiBaseUris(), is(uris));
    }

    @Test
    public void apiConfigShouldNotSetMongoPrefsOnScanResult() throws Exception{
        setSingleValidApiOnlyWithIntentResult();
        assertThat(prefs.isMongoUploadEnabled(), is(false));
    }

    @Test
    public void apiConfigShouldNotSetMqttPrefsOnScanResult() throws Exception {
        setSingleValidApiOnlyWithIntentResult();
        assertThat(prefs.isMqttEnabled(), is(false));
    }

    @Test
    public void multipleApiUriConfigShouldEnableApiPrefsOnScanResult() throws Exception {
        setMultipleValidApiOnlyWithIntentResult();
        assertThat(prefs.isRestApiEnabled(), is(true));
    }

    @Test
    public void multipleApiUriConfigShouldNotEnableMongoPrefsOnScanResult() throws Exception {
        setMultipleValidApiOnlyWithIntentResult();
        assertThat(prefs.isMongoUploadEnabled(), is(false));
    }

    @Test
    public void multipleValidApiUriConfigShouldEnableApiUriPrefsOnScanResult() throws Exception {
        setMultipleValidApiOnlyWithIntentResult();
        assertThat(prefs.isRestApiEnabled(), is(true));
    }

    @Test
    public void multipleValidApiUriConfigShouldSetApiUriPrefsOnScanResult() throws Exception {
        List<String> uris = new ArrayList<>();
        uris.add("http://abc@test.com/v1");
        uris.add("http://test.com/");
        setMultipleValidApiOnlyWithIntentResult();
        assertThat(prefs.getRestApiBaseUris(), is(uris));
    }

    @Test
    public void mongoAndApiConfigShouldEnableApiPrefsOnScanResult() throws Exception {
        setSingleValidApiAndMongoWithIntentResult();
        assertThat(prefs.isRestApiEnabled(), is(true));
    }

    @Test
    public void mongoAndApiConfigShouldEnableMongoPrefsOnScanResult() throws Exception {
        setSingleValidApiAndMongoWithIntentResult();
        assertThat(prefs.isMongoUploadEnabled(), is(true));
    }

    @Test
    public void mongoAndApiConfigShouldSetMongoPrefsOnScanResult() throws Exception {
        setSingleValidApiAndMongoWithIntentResult();
        assertThat(prefs.getMongoClientUri(), is("mongodb://user:pass@test.com/cgm_data"));
    }

    @Test
    public void mongoAndApiConfigShouldSetApiPrefsOnScanResult() throws Exception {
        setSingleValidApiAndMongoWithIntentResult();
        List<String> uris = new ArrayList<>();
        uris.add("http://abc@test.com/");
        assertThat(prefs.getRestApiBaseUris(), is(uris));
    }


    @Test
    public void shouldStartScanActivity(){
        AndroidBarcode barcode = new AndroidBarcode(activity);
        barcode.scan();
        Intent intent = getShadowApplication().getNextStartedActivity();
        assertThat(intent.getComponent().getClassName(), is(CaptureActivity.class.getName()));
    }

    @Test
    public void validMongoOnlyShouldSetDefaultSgCollectionForOnlyMongoUriSet(){
        setValidMongoOnlyWithIntentResult();
        assertThat(prefs.getMongoCollection(), is(getShadowApplication().getApplicationContext().getString(R.string.pref_default_mongodb_collection)));
    }

    @Test
    public void validMongoOnlyShouldSetDefaultDeviceStatusCollectionForOnlyMongoUriSet(){
        setValidMongoOnlyWithIntentResult();
        assertThat(prefs.getMongoDeviceStatusCollection(), is(getShadowApplication().getApplicationContext()
                        .getString(R.string.pref_default_mongodb_device_status_collection)));
    }

    @Test
    public void invalidJsonShouldNotEnableMongo(){
        setInvalidJsonWithIntentResult();
        assertThat(prefs.isMongoUploadEnabled(), is(false));
    }

    @Test
    public void invalidJsonShouldNotEnableApi(){
        setInvalidJsonWithIntentResult();
        assertThat(prefs.isRestApiEnabled(), is(false));
    }

    @Test
    public void setEmptyApiConfigShouldNotEnableApi(){
        setEmptyValidApiOnlyWithIntentResult();
        assertThat(prefs.isRestApiEnabled(), is(false));
    }

    @Test
    public void setEmptyApiConfigShouldNotEnableMongo(){
        setEmptyValidApiOnlyWithIntentResult();
        assertThat(prefs.isMongoUploadEnabled(), is(false));
    }

    @Test
    public void setEmptyMongoConfigShouldNotEnableApi(){
        setEmptyValidMongoOnlyWithIntentResult();
        assertThat(prefs.isRestApiEnabled(), is(false));
    }

    @Test
    public void setEmptyMongoConfigShouldNotEnableMongo(){
        setEmptyValidMongoOnlyWithIntentResult();
        assertThat(prefs.isMongoUploadEnabled(), is(false));
    }

    @Test
    public void invalidConfigShouldNotEnableMongo(){
        setInvalidConfigWithValidJson();
        assertThat(prefs.isMongoUploadEnabled(), is(false));
    }

    @Test
    public void invalidConfigShouldNotEnableApi(){
        setInvalidConfigWithValidJson();
        assertThat(prefs.isRestApiEnabled(), is(false));
    }

    private Intent createFakeScanIntent(String jsonString){
        Intent intent = new Intent(AndroidBarcode.SCAN_INTENT);
        intent.putExtra("SCAN_RESULT", jsonString);
        intent.putExtra("SCAN_RESULT_FORMAT", "QR_CODE");
        intent.putExtra("SCAN_RESULT_BYTES", new byte[0]);
        intent.putExtra("SCAN_RESULT_ORIENTATION", Integer.MIN_VALUE);
        intent.putExtra("SCAN_RESULT_ERROR_CORRECTION_LEVEL", "");
        return intent;
    }
}