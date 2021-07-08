package ch.admin.bag.covidcertificate.backend.transformation.ws.client.deserializer;

import ch.admin.bag.covidcertificate.sdk.core.models.healthcert.CovidCertificate;
import ch.admin.bag.covidcertificate.sdk.core.models.healthcert.eu.DccCert;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;

public class CustomCovidCertificateDeserializer extends StdDeserializer<CovidCertificate> {

    public CustomCovidCertificateDeserializer() {
        this(null);
    }

    protected CustomCovidCertificateDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public CovidCertificate deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        return p.readValueAs(DccCert.class);
    }
}
