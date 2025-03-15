package com.example.application.data;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
/**
 * <p>
 * Title: {@link BCiUserRepository}
 * </p>
 * <p>
 * Description: User epository
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025 Baader Computer
 * </p>
 * <p>
 * Company: Baader Computer
 * </p>
 *
 * @author marketa.jarkovska
 * @date 01.03.2025 21:18
 */
public interface BCiUserRepository extends JpaRepository<BCcUserEO, Long>, JpaSpecificationExecutor<BCcUserEO> {

    Optional<BCcUserEO> findByUsername(String aUsername);
}
