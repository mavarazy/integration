package com.clemble.casino.integration.player;

import static com.google.common.base.Preconditions.checkNotNull;

import com.clemble.casino.client.ClembleCasinoOperations;
import com.clemble.casino.client.ClembleCasinoRegistrationOperations;
import com.clemble.casino.integration.event.EventListenerOperationsFactory;
import com.clemble.casino.integration.goal.IntegrationGoalOperationsFactory;
import com.clemble.casino.player.PlayerProfile;
import com.clemble.casino.registration.*;
import com.clemble.casino.registration.service.PlayerSocialRegistrationService;
import com.clemble.casino.server.connection.controller.PlayerFriendInvitationServiceController;
import com.clemble.casino.server.email.controller.PlayerEmailServiceController;
import com.clemble.casino.server.post.controller.PlayerFeedServiceController;
import com.clemble.casino.server.registration.controller.PlayerPasswordResetServiceController;
import com.clemble.casino.server.registration.controller.PlayerRegistrationController;
import com.clemble.casino.social.SocialAccessGrant;
import com.clemble.casino.social.SocialConnectionData;
import com.clemble.casino.server.connection.controller.PlayerConnectionServiceController;
import com.clemble.casino.server.payment.controller.PaymentTransactionServiceController;
import com.clemble.casino.server.payment.controller.PlayerAccountServiceController;
import com.clemble.casino.server.profile.controller.PlayerImageServiceController;
import com.clemble.casino.server.profile.controller.PlayerProfileServiceController;
import com.clemble.server.tag.controller.PlayerTagServiceController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.clemble.casino.server.notification.controller.PlayerNotificationServiceController;
import org.springframework.mock.web.MockHttpServletResponse;

public class IntegrationClembleCasinoRegistrationOperations implements ClembleCasinoRegistrationOperations {

    final private String host;
    final private ObjectMapper objectMapper;
    final private PlayerRegistrationController registrationController;
    final private PlayerSocialRegistrationService socialRegistrationController;
    final private PlayerProfileServiceController profileOperations;
    final private PlayerImageServiceController imageService;
    final private PlayerConnectionServiceController connectionService;
    final private PlayerFriendInvitationServiceController invitationService;
    final private PlayerAccountServiceController paymentService;
    final private PaymentTransactionServiceController paymentTransactionService;
    final private EventListenerOperationsFactory listenerOperations;
    final private IntegrationGoalOperationsFactory goalOperationsFactory;
    final private PlayerNotificationServiceController notificationServiceController;
    final private PlayerFeedServiceController feedServiceController;
    final private PlayerPasswordResetServiceController passwordResetService;
    final private PlayerEmailServiceController emailServiceController;
    final private PlayerTagServiceController tagServiceController;

    public IntegrationClembleCasinoRegistrationOperations(
        String host,
        ObjectMapper objectMapper,
        EventListenerOperationsFactory listenerOperations,
        PlayerRegistrationController registrationController,
        PlayerSocialRegistrationService socialRegistrationController,
        PlayerProfileServiceController profileOperations,
        PlayerImageServiceController imageService,
        PlayerConnectionServiceController connectionService,
        PlayerFriendInvitationServiceController invitationService,
        PlayerAccountServiceController accountOperations,
        PaymentTransactionServiceController paymentTransactionService,
        IntegrationGoalOperationsFactory goalOperationsFactory,
        PlayerNotificationServiceController notificationServiceController,
        PlayerFeedServiceController feedServiceController,
        PlayerPasswordResetServiceController passwordResetService,
        PlayerEmailServiceController emailServiceController,
        PlayerTagServiceController tagServiceController) {
        this.host = checkNotNull(host);
        this.objectMapper = checkNotNull(objectMapper);
        this.registrationController = checkNotNull(registrationController);
        this.socialRegistrationController = checkNotNull(socialRegistrationController);
        this.listenerOperations = checkNotNull(listenerOperations);
        this.profileOperations = checkNotNull(profileOperations);
        this.imageService = checkNotNull(imageService);
        this.connectionService = checkNotNull(connectionService);
        this.invitationService = checkNotNull(invitationService);
        this.paymentService = checkNotNull(accountOperations);
        this.paymentTransactionService = checkNotNull(paymentTransactionService);
        this.goalOperationsFactory = checkNotNull(goalOperationsFactory);
        this.notificationServiceController = checkNotNull(notificationServiceController);
        this.feedServiceController = feedServiceController;
        this.passwordResetService = passwordResetService;
        this.emailServiceController = emailServiceController;
        this.tagServiceController = tagServiceController;
    }

    @Override
    public ClembleCasinoOperations login(PlayerLoginRequest playerCredentials) {
        PlayerLoginRequest response = registrationController.httpLogin(playerCredentials, new MockHttpServletResponse());
        return create(response.getPlayer());
    }

    @Override
    public ClembleCasinoOperations register(PlayerCredential playerCredential, PlayerProfile playerProfile) {
        PlayerRegistrationRequest loginRequest = PlayerRegistrationRequest.create(playerCredential, playerProfile);
        PlayerRegistrationRequest response = registrationController.httpRegister(loginRequest, new MockHttpServletResponse());
        return create(response.getPlayer());
    }

    @Override
    public ClembleCasinoOperations register(PlayerCredential playerCredential, SocialConnectionData socialConnectionData) {
        PlayerSocialRegistrationRequest loginRequest = new PlayerSocialRegistrationRequest(playerCredential, socialConnectionData);
        String player = socialRegistrationController.register(loginRequest);
        return create(player);
    }

    @Override
    public ClembleCasinoOperations register(PlayerCredential playerCredential, SocialAccessGrant accessGrant) {
        PlayerSocialGrantRegistrationRequest loginRequest = new PlayerSocialGrantRegistrationRequest(playerCredential, accessGrant);
        String token = socialRegistrationController.register(loginRequest);
        return create(token);
    }

    private ClembleCasinoOperations create(String player) {
        return new IntegrationClembleCasinoOperations(
            host,
            objectMapper,
            player,
            profileOperations,
            imageService,
            connectionService,
            invitationService,
            paymentService,
            paymentTransactionService,
            listenerOperations,
            goalOperationsFactory.construct(player),
            notificationServiceController,
            feedServiceController,
            passwordResetService,
            emailServiceController,
            tagServiceController
        );
    }

}