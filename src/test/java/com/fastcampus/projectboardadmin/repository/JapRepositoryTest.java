package com.fastcampus.projectboardadmin.repository;

import com.fastcampus.projectboardadmin.domain.UserAccount;
import com.fastcampus.projectboardadmin.domain.constant.RoleType;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DisplayName("Jpa 연결 테스트")
@Import(JapRepositoryTest.TestJapConfig.class)
@DataJpaTest
class JapRepositoryTest {

    private final UserAccountRepository userAccountRepository;

    public JapRepositoryTest(@Autowired UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @DisplayName("회원 정보 select 테스트")
    @Test
    void givenUserAccounts_whenSelecting_thenWorksFine() throws Exception {
        //given

        //when
        List<UserAccount> userAccounts = userAccountRepository.findAll();

        //then
        Assertions.assertThat(userAccounts)
                .isNotNull()
                .hasSize(4);
    }

    @DisplayName("회원 정보 insert test")
    @Test
    void givenUserAccount_whenInserting_thenWorksFine() throws Exception {


        //given
        long previousCount = userAccountRepository.count();

        UserAccount userAccount = UserAccount.of("test", "pw", Set.of(RoleType.DEVELOPER), null, null, null);

        //when
        userAccountRepository.save(userAccount);

        //then
        Assertions.assertThat(userAccountRepository.count()).isEqualTo(previousCount + 1);

    }


    @DisplayName("회원 정보 update 테스트")
    @Test
    void givenUserAccountAndRoleType_whenUpdating_thenWorksFine() throws Exception {
        //given
        UserAccount userAccount = userAccountRepository.getReferenceById("uno");
        //when
        userAccount.addRoleType(RoleType.DEVELOPER);

        //then
        userAccount.addRoleTypes(List.of(RoleType.USER, RoleType.USER));

        userAccount.removeRoleType(RoleType.ADMIN);

        UserAccount updatedAccount = userAccountRepository.saveAndFlush(userAccount);

        Assertions.assertThat(updatedAccount)
                .hasFieldOrPropertyWithValue("userId", "uno")
                .hasFieldOrPropertyWithValue("roleTypes", Set.of(RoleType.DEVELOPER, RoleType.USER));

    }

    @Transactional
    @DisplayName("회원 정보 delete 테스트")
    @Test
    void givenUserAccount_whenDeleting_thenWorksFine() throws Exception {

        long previousCount = userAccountRepository.count();

        UserAccount userAccount = userAccountRepository.findById("uno")
                .orElseThrow(() -> new EntityNotFoundException("User not found!"));

        // When
        userAccountRepository.delete(userAccount);

        // Then
        assertThat(userAccountRepository.count()).isEqualTo(previousCount - 1);
    }


    @EnableJpaAuditing
    @TestConfiguration
    static class TestJapConfig{

        @Bean
        AuditorAware<String> auditorAware(){
            return () -> Optional.of("uno");
        }
    }
}
