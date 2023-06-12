package org.sonicframework.core.encrypt;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
* @author lujunyi
*/
public interface RsakeyProviderService {

	PrivateKey generatePrivateKey();
	PublicKey generatePublicKey();
}
