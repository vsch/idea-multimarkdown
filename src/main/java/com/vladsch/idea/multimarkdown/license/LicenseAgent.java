/*
 * Copyright (c) 2015-2015 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
 *
 * This code is private property of the copyright holder and cannot be used without
 * having obtained a license or prior written permission of the of the copyright holder.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package com.vladsch.idea.multimarkdown.license;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.util.net.HttpConfigurable;
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.crypto.NoSuchPaddingException;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LicenseAgent {
    private static final Logger logger = Logger.getLogger(LicenseAgent.class);
    private static final boolean LOG_AGENT_INFO = false;

    final static private String agent_signature = "475f99b03f6ec213729d7f5d577c80aa";
    final static private String license_pub = "-----BEGIN PUBLIC KEY-----\n" +
            "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAlnefMGqNu1Q9hcI2Rd8G\n" +
            "xyKlXQIFyXWIkYODRrLjvEwXYw0yksgjZeIC4g+hakyQNiN+TGE/xvo3fqB0CU4A\n" +
            "aE33Mu7jB27dt1IItcmBhJBwIhmZDc0SWNj6ywvnLeUU/NSWWbJ1SaXzPQJ2Mm5T\n" +
            "Mr3wDFhCTp80pN4svOQmdQPFSKXwdGI+n8gJvc28vRgD8As2XxgkYsZPNjefOsla\n" +
            "GHS8CNw6uI8Ijcf9hfX22twQZ+auYNL/vqtBEKq2jNLwoHTo68s+0JWJu2YILlIe\n" +
            "VQzXcXZyhhAVdZrMNGhBPiXUia6YrJpqZNDZ35CE+Y6ecs9c5AG2wpFJHnic2cjZ\n" +
            "Kh+ba83DpA3GxYa1OGMGZNaIqCjuK7A82ZPriXsoxL6YJzqSlbF/2l2x4Y3VoVTF\n" +
            "LWKEpjvLOuDOev0CH41nzkGD4Yo5CwHPZFun/WekqUBUXtxR/uH0ThoxV93exTLc\n" +
            "YwWC5GqVZfN38Ye7iDljIFVzxxP3unBy0FItg52407CZyH/gTB+Zm++fZJdKbZcl\n" +
            "UFvxtACEJvdgdM30FHuQlvS53mEXOMAzpJPVZu2gRoTl8cSO3GKxaNP9dmPCzD4a\n" +
            "gO/kVrO/c6DerwWvCJJhifKlDc6CfjM3FfWsVI2gw3WduFPJcIsLxlUqzBh95rA1\n" +
            "R+BTr2n3DV41OK5AwtCQO40CAwEAAQ==\n" +
            "-----END PUBLIC KEY-----\n";

    final static private String licenseHeader = "-----BEGIN IDEA-MULTIMARKDOWN LICENSE-----";
    final static private String licenseFooter = "-----END IDEA-MULTIMARKDOWN LICENSE-----";
    final static private String activationHeader = "-----BEGIN IDEA-MULTIMARKDOWN ACTIVATION-----";
    final static private String activationFooter = "-----END IDEA-MULTIMARKDOWN ACTIVATION-----";

    final static private String siteURL = "https://vladsch.com";
    //final static private String siteURL = "http://vladsch.dev";

    final static private String trialURL = siteURL + "/product/multimarkdown/json/trial";
    final static private String licenseURL = siteURL + "/product/multimarkdown/json/license";
    final static private String tryPageURL = siteURL + "/product/multimarkdown/try";
    final static private String buyPageURL = siteURL + "/product/multimarkdown/buy";
    private static final String ACTIVATION_EXPIRES = "activation_expires";
    private static final String LICENSE_EXPIRES = "license_expires";
    private static final String PRODUCT_VERSION = "product_version";
    private static final String AGENT_SIGNATURE = "agent_signature";
    private static final String LICENSE_CODE = "license_code";
    private static final String LICENSE_TYPE = "license_type";
    private static final String LICENSE_FEATURES = "license_features";
    private static final String LICENSE_FEATURE_LIST = "feature_list";
    private static final String ACTIVATION_CODE = "activation_code";
    private static final String HOST_PRODUCT = "host_product";
    private static final String HOST_NAME = "host_name";
    private static final String ACTIVATED_ON = "activated_on";
    private static final String STATUS = "status";
    private static final String MESSAGE = "message";
    private static final String STATUS_DISABLE = "disable";
    private static final String STATUS_OK = "ok";
    private static final String STATUS_ERROR = "error";
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String LICENSE_TYPE_TRIAL = "trial";
    public static final String LICENSE_TYPE_SUBSCRIPTION = "subscription";
    public static final String LICENSE_TYPE_LICENSE = "license";

    private String license_code;
    private String activation_code;
    private JsonObject activation;
    private String license_expires;
    private String product_version;
    private JsonObject json; // last server response
    private boolean remove_license;
    private String license_type;
    private int license_features;
    private JsonObject feature_list;

    public LicenseAgent(LicenseAgent other) {
        updateFrom(other);
    }

    public void updateFrom(LicenseAgent other) {
        this.license_code = other.license_code;
        this.activation_code = other.activation_code;
        this.activation = other.activation;
        this.license_expires = other.license_expires;
        this.product_version = other.product_version;
        this.json = other.json;
        this.remove_license = other.remove_license;
        this.license_type = other.license_type;
        this.license_features = other.license_features;
        this.feature_list = other.feature_list;
    }

    public boolean isRemoveLicense() {
        return remove_license;
    }

    @NotNull
    public static String getTrialLicenseURL() {
        return tryPageURL;
    }

    public void setLicenseCode(String license_code) {
        String trimmed = license_code.trim();
        if (!trimmed.equals(this.license_code == null ? "" : this.license_code)) {
            this.license_code = trimmed;
            setActivationCode(null);
        }
    }

    public void setLicenseActivationCodes(String license_code, String activation_code) {
        String trimmed = license_code.trim();
        if (!trimmed.equals(this.license_code == null ? "" : this.license_code)) {
            this.license_code = trimmed;
            setActivationCode(activation_code);
        } else {
            setActivationCode(activation_code);
        }
    }

    public void setActivationCode(String activation_code) {
        if (activation_code != null) {
            String trimmed = activation_code.trim();
            if (!trimmed.equals(this.activation_code == null ? "" : this.activation_code)) {
                this.activation_code = trimmed;
                this.json = null;
                this.activation = null;

                if (!isValidActivation()) {
                    this.activation_code = null;
                    this.json = null;
                    this.activation = null;
                }
            }
        } else {
            this.activation_code = null;
            this.json = null;
            this.activation = null;
        }
    }

    @NotNull
    public static String getLicenseURL() {
        return buyPageURL;
    }

    @NotNull
    public String licenseCode() {
        return license_code != null ? license_code : "";
    }

    @Nullable
    public String getLicenseExpires() {
        return license_expires;
    }

    @Nullable
    public String getProductVersion() {
        return product_version;
    }

    @NotNull
    public String getMessage() {
        String message;
        return json != null ? ((message = json.getString(MESSAGE)) != null ? message : "") : "";
    }

    @Nullable
    public String getStatus() {
        return json != null ? json.getString(STATUS) : null;
    }

    @NotNull
    public String activationCode() {
        return activation_code != null ? activation_code : "";
    }

    @Nullable
    public JsonObject getActivation() {
        return activation;
    }

    public LicenseAgent() {

    }

    public boolean getLicenseCode(LicenseRequest licenseRequest) {
        licenseRequest.agent_signature = agent_signature;
        try {
            final URL url = new URL(licenseURL);
            final HttpConfigurable httpConfigurable = (HttpConfigurable) ApplicationManager.getApplication().getComponent("HttpConfigurable");

            final URLConnection urlConnection = httpConfigurable != null ? httpConfigurable.openConnection(licenseURL) : url.openConnection();

            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            urlConnection.connect();
            final OutputStream outputStream = urlConnection.getOutputStream();
            if (LOG_AGENT_INFO) logger.info(licenseRequest.toJsonString());
            outputStream.write((licenseRequest.toJsonString()).getBytes("UTF-8"));
            outputStream.flush();
            final InputStream inputStream = urlConnection.getInputStream();
            //BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            //StringBuilder sb = new StringBuilder();
            //
            //String line = null;
            //try {
            //    while ((line = reader.readLine()) != null) {
            //        sb.append(line).append('\n');
            //    }
            //} catch (IOException e) {
            //    e.printStackTrace();
            //} finally {
            //    try {
            //        inputStream.close();
            //    } catch (IOException e) {
            //        e.printStackTrace();
            //    }
            //}
            JsonReader jsonReader = Json.createReader(inputStream);
            json = jsonReader.readObject();
            String status = json.getString(STATUS, "");
            String message = json.getString(MESSAGE, "");
            if (status.equals(STATUS_OK)) {
                if ((licenseRequest.hasLicenseCode() || json.containsKey(LICENSE_CODE)) && json.containsKey(ACTIVATION_CODE)) {
                    if (json.containsKey(LICENSE_CODE)) this.license_code = json.getString(LICENSE_CODE);
                    this.activation_code = json.getString(ACTIVATION_CODE);
                    return true;
                } else {
                    if (LOG_AGENT_INFO) logger.info("License server did not reply with a valid response");
                }
            } else {
                if (status.equals(STATUS_DISABLE)) {
                    // remove license information from this plugin
                    if (LOG_AGENT_INFO) logger.info("License server requested license removal from this host");
                    license_code = null;
                    activation_code = null;
                    activation = null;
                    remove_license = true;
                    status = STATUS_ERROR;
                }
                if (LOG_AGENT_INFO) logger.info("License server replied with status: " + status + ", message: " + message);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean isValidLicense() {
        if (license_code != null) {
            int headerPos = license_code.indexOf(licenseHeader);
            int footerPos = license_code.lastIndexOf(licenseFooter);
            if (headerPos >= 0 && footerPos > headerPos) {
                return true;
            }
        }
        return false;
    }

    public boolean isValidActivation() {
        if (activation_code != null) {
            if (activation != null) {
                if (isActivationExpired()) {
                    activation = null;
                    activation_code = null;
                    return false;
                }
            }

            if (activation == null) {
                int headerPos = activation_code.indexOf(activationHeader);
                int footerPos = activation_code.lastIndexOf(activationFooter);
                if (headerPos >= 0 && footerPos > headerPos) {
                    try {
                        LicenseKey licenseKey = new LicenseKey(license_pub);
                        String activationJson = licenseKey.decrypt(activation_code.substring(headerPos + activationHeader.length(), footerPos));

                        if (activationJson != null) {
                            byte[] bytes = activationJson.getBytes("UTF-8");
                            InputStream stream = new ByteArrayInputStream(bytes);
                            JsonReader jsonReader = Json.createReader(stream);
                            activation = jsonReader.readObject();
                        }
                    } catch (NoSuchPaddingException ignored) {
                    } catch (NoSuchAlgorithmException ignored) {
                    } catch (IOException ignored) {
                    } catch (InvalidKeySpecException ignored) {
                    } catch (Exception ignored) {
                    }
                }
            }

            if (activation != null && activation.containsKey(AGENT_SIGNATURE) && activation.getString(AGENT_SIGNATURE, "").equals(agent_signature)
                    && activation.containsKey(LICENSE_EXPIRES)
                    && activation.containsKey(LICENSE_TYPE)
                    && activation.containsKey(LICENSE_FEATURES)
                    && activation.containsKey(LICENSE_FEATURE_LIST)
                    && activation.containsKey(PRODUCT_VERSION)
                    && activation.containsKey(HOST_NAME)
                    && activation.containsKey(HOST_PRODUCT)
                    && activation.getString(LICENSE_TYPE) != null
                    && activation.getInt(LICENSE_FEATURES) != 0
                    ) {
                try {
                    license_expires = activation.getString(LICENSE_EXPIRES);
                    product_version = activation.getString(PRODUCT_VERSION);
                    license_type = activation.getString(LICENSE_TYPE);
                    license_features = activation.getInt(LICENSE_FEATURES);
                    feature_list = activation.getJsonObject(LICENSE_FEATURE_LIST);
                    return true;
                } catch (JsonException ignored) {
                    if (LOG_AGENT_INFO) logger.info("Activation JsonException " + ignored);
                } catch (ClassCastException ignored) {
                    if (LOG_AGENT_INFO) logger.info("Activation ClassCastException " + ignored);
                } catch (Exception ignored) {
                    if (LOG_AGENT_INFO) logger.info("Activation Exception " + ignored);
                }
            }
        }
        return false;
    }

    @NotNull
    public String getLicenseType() {
        return license_type;
    }

    public int getLicenseFeatures() {
        return license_features;
    }

    @NotNull
    public String getLicenseExpiration() {
        if (activation != null && activation.containsKey(LICENSE_EXPIRES)) {
            return activation.getString(LICENSE_EXPIRES);
        }
        return "";
    }

    @NotNull
    public String getHostName() {
        if (activation != null && activation.containsKey(HOST_NAME)) {
            return activation.getString(HOST_NAME);
        }
        return "";
    }

    @NotNull
    public String getHostProduct() {
        if (activation != null && activation.containsKey(HOST_PRODUCT)) {
            return activation.getString(HOST_PRODUCT);
        }
        return "";
    }

    @NotNull
    public String getActivatedOn() {
        if (activation != null && activation.containsKey(ACTIVATED_ON)) {
            return activation.getString(ACTIVATED_ON);
        }
        return "";
    }

    public int getLicenseExpiringIn() {
        // see if the license expiration is more than i days away
        if (activation != null && activation.containsKey(LICENSE_EXPIRES)) {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            try {
                String expires = activation.getString(LICENSE_EXPIRES);
                Date expiration = df.parse(expires);
                Date today = new Date();
                int days = (int) Math.floor((expiration.getTime() - today.getTime()) / (1000 * 60 * 60 * 24));
                return days + 1;
            } catch (ParseException ignored) {
            }
        }
        return 0;
    }

    public boolean isActivationExpired() {
        // see if the activation has expired
        if (activation != null) {
            if (product_version == null || !product_version.equals(MultiMarkdownPlugin.getProductVersion())
                    || !getHostName().equals(LicenseRequest.getHostName())
                    || !getHostProduct().equals(LicenseRequest.getHostProduct())
                    ) {
                return true;
            }

            if (activation.containsKey(ACTIVATION_EXPIRES)) {
                DateFormat df = new SimpleDateFormat(DATE_FORMAT);
                try {
                    String expires = activation.getString(ACTIVATION_EXPIRES);
                    Date expiration = df.parse(expires);
                    Date today = new Date();
                    int days = (int) Math.floorDiv(expiration.getTime() - today.getTime(), (1000 * 60 * 60 * 24));
                    return days < 0;
                } catch (ParseException ignored) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
}
