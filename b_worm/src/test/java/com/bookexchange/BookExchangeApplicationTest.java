package com.bookexchange;

import com.bookexchange.model.User;
import com.bookexchange.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class BookExchangeApplicationTest {

    @Test
    void testDataLoader_SeedsDefaultUsers_WhenRepositoryIsEmpty() throws Exception {
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

        when(userRepository.count()).thenReturn(0L);
        when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> "enc-" + invocation.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CommandLineRunner runner = new BookExchangeApplication().dataLoader(userRepository, passwordEncoder);
        runner.run();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(3)).save(userCaptor.capture());

        List<User> savedUsers = userCaptor.getAllValues();
        assertThat(savedUsers).extracting(User::getUsername)
                .containsExactly("admin", "member1", "member2");
        assertThat(savedUsers).allMatch(User::isEnabled);
        assertThat(savedUsers).extracting(User::getPassword)
                .allMatch(password -> password.startsWith("enc-"));
        verify(passwordEncoder, times(3)).encode(anyString());
    }

    @Test
    void testDataLoader_DoesNothing_WhenUsersAlreadyExist() throws Exception {
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

        when(userRepository.count()).thenReturn(2L);

        CommandLineRunner runner = new BookExchangeApplication().dataLoader(userRepository, passwordEncoder);
        runner.run();

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void testMain_HasExpectedStaticSignature() throws Exception {
        Method mainMethod = BookExchangeApplication.class.getMethod("main", String[].class);
        assertThat(Modifier.isStatic(mainMethod.getModifiers())).isTrue();
        assertThat(mainMethod.getReturnType()).isEqualTo(Void.TYPE);
    }
}
