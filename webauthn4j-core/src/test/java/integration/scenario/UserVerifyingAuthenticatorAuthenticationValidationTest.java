package integration.scenario;

import com.webauthn4j.WebAuthnAuthenticationContext;
import com.webauthn4j.attestation.AttestationObject;
import com.webauthn4j.attestation.statement.COSEAlgorithmIdentifier;
import com.webauthn4j.authenticator.Authenticator;
import com.webauthn4j.client.CollectedClientData;
import com.webauthn4j.client.Origin;
import com.webauthn4j.client.challenge.Challenge;
import com.webauthn4j.client.challenge.DefaultChallenge;
import com.webauthn4j.converter.AttestationObjectConverter;
import com.webauthn4j.server.ServerProperty;
import com.webauthn4j.test.TestUtil;
import com.webauthn4j.test.authenticator.model.WebAuthnModelAuthenticatorAdaptor;
import com.webauthn4j.test.platform.*;
import com.webauthn4j.validator.WebAuthnAuthenticationContextValidator;
import com.webauthn4j.validator.exception.*;
import org.junit.Test;

import java.util.Collections;

import static com.webauthn4j.client.CollectedClientData.TYPE_WEBAUTHN_CREATE;
import static com.webauthn4j.client.CollectedClientData.TYPE_WEBAUTHN_GET;

public class UserVerifyingAuthenticatorAuthenticationValidationTest {

    private Origin origin = new Origin("http://example.com");
    private WebAuthnModelAuthenticatorAdaptor webAuthnModelAuthenticatorAdaptor = new WebAuthnModelAuthenticatorAdaptor();
    private ClientPlatform clientPlatform = new ClientPlatform(origin, webAuthnModelAuthenticatorAdaptor);
    private WebAuthnAuthenticationContextValidator target = new WebAuthnAuthenticationContextValidator();

    @Test
    public void validate_test() {
        String rpId = "example.com";
        long timeout = 0;
        Challenge challenge = new DefaultChallenge();

        // create
        AttestationObject attestationObject = createAttestationObject(rpId, challenge);

        // get
        PublicKeyCredentialRequestOptions credentialRequestOptions = new PublicKeyCredentialRequestOptions(
                challenge,
                timeout,
                rpId,
                null,
                UserVerificationRequirement.REQUIRED,
                null
        );
        PublicKeyCredential<AuthenticatorAssertionResponse> publicKeyCredential = clientPlatform.get(credentialRequestOptions);
        AuthenticatorAssertionResponse authenticationRequest = publicKeyCredential.getAuthenticatorResponse();

        ServerProperty serverProperty = new ServerProperty(origin, rpId, challenge, null);

        WebAuthnAuthenticationContext authenticationContext =
                new WebAuthnAuthenticationContext(
                        publicKeyCredential.getRawId(),
                        authenticationRequest.getClientDataJSON(),
                        authenticationRequest.getAuthenticatorData(),
                        authenticationRequest.getSignature(),
                        serverProperty
                );
        Authenticator authenticator = TestUtil.createAuthenticator(attestationObject);
        target.validate(authenticationContext, authenticator, true);

    }

    @Test
    public void validate_assertion_with_tokenBinding_test() {
        String rpId = "example.com";
        long timeout = 0;
        Challenge challenge = new DefaultChallenge();

        // create
        AttestationObject attestationObject = createAttestationObject(rpId, challenge);

        // get
        PublicKeyCredentialRequestOptions credentialRequestOptions = new PublicKeyCredentialRequestOptions(
                challenge,
                timeout,
                rpId,
                null,
                UserVerificationRequirement.REQUIRED,
                null
        );

        byte[] tokenBindingId = new byte[]{0x01, 0x23, 0x45};
        CollectedClientData collectedClientData = clientPlatform.createCollectedClientData(TYPE_WEBAUTHN_GET, challenge, tokenBindingId);
        PublicKeyCredential<AuthenticatorAssertionResponse> publicKeyCredential = clientPlatform.get(credentialRequestOptions, collectedClientData);
        AuthenticatorAssertionResponse authenticationRequest = publicKeyCredential.getAuthenticatorResponse();

        ServerProperty serverProperty = new ServerProperty(origin, rpId, challenge, tokenBindingId);

        WebAuthnAuthenticationContext authenticationContext =
                new WebAuthnAuthenticationContext(
                        publicKeyCredential.getRawId(),
                        authenticationRequest.getClientDataJSON(),
                        authenticationRequest.getAuthenticatorData(),
                        authenticationRequest.getSignature(),
                        serverProperty
                );
        Authenticator authenticator = TestUtil.createAuthenticator(attestationObject);
        target.validate(authenticationContext, authenticator, true);

    }


    @Test(expected = MaliciousDataException.class)
    public void validate_assertion_test_with_bad_clientData_type() {
        String rpId = "example.com";
        long timeout = 0;
        Challenge challenge = new DefaultChallenge();

        // create
        AttestationObject attestationObject = createAttestationObject(rpId, challenge);

        // get
        PublicKeyCredentialRequestOptions credentialRequestOptions = new PublicKeyCredentialRequestOptions(
                challenge,
                timeout,
                rpId,
                null,
                UserVerificationRequirement.REQUIRED,
                null
        );
        CollectedClientData collectedClientData = clientPlatform.createCollectedClientData(TYPE_WEBAUTHN_CREATE, challenge); // bad clientData type
        PublicKeyCredential<AuthenticatorAssertionResponse> publicKeyCredential = clientPlatform.get(credentialRequestOptions, collectedClientData);
        AuthenticatorAssertionResponse authenticationRequest = publicKeyCredential.getAuthenticatorResponse();

        ServerProperty serverProperty = new ServerProperty(origin, rpId, challenge, null);

        WebAuthnAuthenticationContext authenticationContext =
                new WebAuthnAuthenticationContext(
                        publicKeyCredential.getRawId(),
                        authenticationRequest.getClientDataJSON(),
                        authenticationRequest.getAuthenticatorData(),
                        authenticationRequest.getSignature(),
                        serverProperty
                );
        Authenticator authenticator = TestUtil.createAuthenticator(attestationObject);
        target.validate(authenticationContext, authenticator, true);

    }

    @Test(expected = BadChallengeException.class)
    public void validate_assertion_with_bad_challenge_test() {
        String rpId = "example.com";
        long timeout = 0;
        Challenge challenge = new DefaultChallenge();
        Challenge badChallenge = new DefaultChallenge();

        // create
        AttestationObject attestationObject = createAttestationObject(rpId, challenge);

        // get
        PublicKeyCredentialRequestOptions credentialRequestOptions = new PublicKeyCredentialRequestOptions(
                badChallenge, //bad challenge
                timeout,
                rpId,
                null,
                UserVerificationRequirement.REQUIRED,
                null
        );
        PublicKeyCredential<AuthenticatorAssertionResponse> publicKeyCredential = clientPlatform.get(credentialRequestOptions);
        AuthenticatorAssertionResponse authenticationRequest = publicKeyCredential.getAuthenticatorResponse();

        ServerProperty serverProperty = new ServerProperty(origin, rpId, challenge, null);

        WebAuthnAuthenticationContext authenticationContext =
                new WebAuthnAuthenticationContext(
                        publicKeyCredential.getRawId(),
                        authenticationRequest.getClientDataJSON(),
                        authenticationRequest.getAuthenticatorData(),
                        authenticationRequest.getSignature(),
                        serverProperty
                );
        Authenticator authenticator = TestUtil.createAuthenticator(attestationObject);
        target.validate(authenticationContext, authenticator, true);

    }

    @Test(expected = BadOriginException.class)
    public void validate_assertion_with_bad_origin_test() {
        String rpId = "example.com";
        long timeout = 0;
        Challenge challenge = new DefaultChallenge();

        // create
        AttestationObject attestationObject = createAttestationObject(rpId, challenge);

        // get
        PublicKeyCredentialRequestOptions credentialRequestOptions = new PublicKeyCredentialRequestOptions(
                challenge,
                timeout,
                rpId,
                null,
                UserVerificationRequirement.REQUIRED,
                null
        );
        clientPlatform.setOrigin(new Origin("https://bad.origin.example.com")); //bad origin
        PublicKeyCredential<AuthenticatorAssertionResponse> publicKeyCredential = clientPlatform.get(credentialRequestOptions);
        AuthenticatorAssertionResponse authenticationRequest = publicKeyCredential.getAuthenticatorResponse();

        ServerProperty serverProperty = new ServerProperty(origin, rpId, challenge, null);

        WebAuthnAuthenticationContext authenticationContext =
                new WebAuthnAuthenticationContext(
                        publicKeyCredential.getRawId(),
                        authenticationRequest.getClientDataJSON(),
                        authenticationRequest.getAuthenticatorData(),
                        authenticationRequest.getSignature(),
                        serverProperty
                );
        Authenticator authenticator = TestUtil.createAuthenticator(attestationObject);
        target.validate(authenticationContext, authenticator, true);

    }

    @Test(expected = TokenBindingException.class)
    public void validate_assertion_with_invalid_tokenBinding_test() {
        String rpId = "example.com";
        long timeout = 0;
        Challenge challenge = new DefaultChallenge();

        // create
        AttestationObject attestationObject = createAttestationObject(rpId, challenge);

        // get
        PublicKeyCredentialRequestOptions credentialRequestOptions = new PublicKeyCredentialRequestOptions(
                challenge,
                timeout,
                rpId,
                null,
                UserVerificationRequirement.REQUIRED,
                null
        );

        byte[] tokenBindingId = new byte[]{0x01, 0x23, 0x45};
        CollectedClientData collectedClientData = clientPlatform.createCollectedClientData(TYPE_WEBAUTHN_GET, challenge, tokenBindingId);
        PublicKeyCredential<AuthenticatorAssertionResponse> publicKeyCredential = clientPlatform.get(credentialRequestOptions, collectedClientData);
        AuthenticatorAssertionResponse authenticationRequest = publicKeyCredential.getAuthenticatorResponse();

        ServerProperty serverProperty = new ServerProperty(origin, rpId, challenge, null);

        WebAuthnAuthenticationContext authenticationContext =
                new WebAuthnAuthenticationContext(
                        publicKeyCredential.getRawId(),
                        authenticationRequest.getClientDataJSON(),
                        authenticationRequest.getAuthenticatorData(),
                        authenticationRequest.getSignature(),
                        serverProperty
                );
        Authenticator authenticator = TestUtil.createAuthenticator(attestationObject);
        target.validate(authenticationContext, authenticator, true);

    }


    @Test(expected = BadRpIdException.class)
    public void validate_bad_rpId_test() {
        String rpId = "example.com";
        String anotherSiteRpId = "another.site.example.net";
        long timeout = 0;
        Challenge challenge = new DefaultChallenge();

        // create
        AttestationObject attestationObject = createAttestationObject(rpId, challenge);

        // get
        PublicKeyCredentialRequestOptions credentialRequestOptions = new PublicKeyCredentialRequestOptions(
                challenge,
                timeout,
                rpId,
                null,
                UserVerificationRequirement.REQUIRED,
                null
        );
        PublicKeyCredential<AuthenticatorAssertionResponse> publicKeyCredential = clientPlatform.get(credentialRequestOptions);
        AuthenticatorAssertionResponse authenticationRequest = publicKeyCredential.getAuthenticatorResponse();

        ServerProperty serverProperty = new ServerProperty(origin, anotherSiteRpId, challenge, null);

        WebAuthnAuthenticationContext authenticationContext =
                new WebAuthnAuthenticationContext(
                        publicKeyCredential.getRawId(),
                        authenticationRequest.getClientDataJSON(),
                        authenticationRequest.getAuthenticatorData(),
                        authenticationRequest.getSignature(),
                        serverProperty
                );
        Authenticator authenticator = TestUtil.createAuthenticator(attestationObject);
        target.validate(authenticationContext, authenticator, true);

    }

    @Test(expected = UserNotVerifiedException.class)
    public void validate_assertion_with_userVerificationDiscouraged_option_test() {
        String rpId = "example.com";
        long timeout = 0;
        Challenge challenge = new DefaultChallenge();

        // create
        AttestationObject attestationObject = createAttestationObject(rpId, challenge);

        // get
        PublicKeyCredentialRequestOptions credentialRequestOptions = new PublicKeyCredentialRequestOptions(
                challenge,
                timeout,
                rpId,
                null,
                UserVerificationRequirement.DISCOURAGED,
                null
        );
        PublicKeyCredential<AuthenticatorAssertionResponse> publicKeyCredential = clientPlatform.get(credentialRequestOptions);
        AuthenticatorAssertionResponse authenticationRequest = publicKeyCredential.getAuthenticatorResponse();

        ServerProperty serverProperty = new ServerProperty(origin, rpId, challenge, null);

        WebAuthnAuthenticationContext authenticationContext =
                new WebAuthnAuthenticationContext(
                        publicKeyCredential.getRawId(),
                        authenticationRequest.getClientDataJSON(),
                        authenticationRequest.getAuthenticatorData(),
                        authenticationRequest.getSignature(),
                        serverProperty
                );
        Authenticator authenticator = TestUtil.createAuthenticator(attestationObject);
        target.validate(authenticationContext, authenticator, true);

    }


    private AttestationObject createAttestationObject(String rpId, Challenge challenge) {
        AuthenticatorSelectionCriteria authenticatorSelectionCriteria = new AuthenticatorSelectionCriteria();
        authenticatorSelectionCriteria.setAuthenticatorAttachment(AuthenticatorAttachment.CROSS_PLATFORM);
        authenticatorSelectionCriteria.setRequireResidentKey(true);
        authenticatorSelectionCriteria.setUserVerificationRequirement(UserVerificationRequirement.REQUIRED);

        PublicKeyCredentialParameters publicKeyCredentialParameters = new PublicKeyCredentialParameters();
        publicKeyCredentialParameters.setAlg(COSEAlgorithmIdentifier.ES256);
        publicKeyCredentialParameters.setType(PublicKeyCredentialType.PublicKey);

        PublicKeyCredentialUserEntity publicKeyCredentialUserEntity = new PublicKeyCredentialUserEntity();
        publicKeyCredentialParameters.setAlg(COSEAlgorithmIdentifier.ES256);
        publicKeyCredentialParameters.setType(PublicKeyCredentialType.PublicKey);

        PublicKeyCredentialCreationOptions credentialCreationOptions = new PublicKeyCredentialCreationOptions();
        credentialCreationOptions.setRp(new PublicKeyCredentialRpEntity(rpId, "example.com"));
        credentialCreationOptions.setChallenge(challenge);
        credentialCreationOptions.setAttestation(AttestationConveyancePreference.NONE);
        credentialCreationOptions.setAuthenticatorSelection(authenticatorSelectionCriteria);
        credentialCreationOptions.setPubKeyCredParams(Collections.singletonList(publicKeyCredentialParameters));
        credentialCreationOptions.setUser(publicKeyCredentialUserEntity);

        AuthenticatorAttestationResponse registrationRequest = clientPlatform.create(credentialCreationOptions).getAuthenticatorResponse();
        AttestationObjectConverter attestationObjectConverter = new AttestationObjectConverter();
        return attestationObjectConverter.convert(registrationRequest.getAttestationObject());
    }
}