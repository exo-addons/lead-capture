/*
 * Copyright (C) 2003-2026 eXo Platform SAS.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.exoplatform.leadcapture.test.service;

import static org.exoplatform.leadcapture.Utils.LEAD_CAPTURE_CONTEXT;
import static org.exoplatform.leadcapture.Utils.LEAD_CAPTURE_SCOPE;
import static org.exoplatform.leadcapture.Utils.LEAD_CAPTURE_SETTINGS_KEY_NAME;
import static org.exoplatform.leadcapture.Utils.USERS_EXPERENCE_GROUP_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.leadcapture.Utils;
import org.exoplatform.leadcapture.dto.LeadCaptureSettings;
import org.exoplatform.leadcapture.services.LeadCaptureSettingsService;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.services.organization.Membership;
import org.exoplatform.services.organization.MembershipHandler;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.security.Identity;
import org.exoplatform.task.dto.StatusDto;

/**
 * Unit test of {@link LeadCaptureSettingsService} against a mocked
 * {@link SettingService}. The platform {@link SettingService} returns
 * {@code null} when the underlying read fails, so the service must never
 * remember a {@code null} read as "no settings".
 */
@RunWith(MockitoJUnitRunner.class)
public class LeadCaptureSettingsServiceTest {

  private static final String        UX_SPACE     = "ux-space";

  private static final String        PROJECT      = "Leads";

  private static final String        TOKEN        = "s3cr3t-token";

  private static final String        NEW_TOKEN    = "n3w-token";

  private static final String        SENDER       = "leads@exoplatform.com";

  private static final String        BLACKLIST    = "spam.com,junk.org";

  private static final String        USER         = "john";

  private static final String        OTHER_GROUP  = "/platform/marketing";

  @Mock
  private SettingService             settingService;

  @Mock
  private OrganizationService        organizationService;

  @Mock
  private MembershipHandler          membershipHandler;

  @Mock
  private Membership                 membership;

  @Mock
  private UserACL                    userACL;

  @Mock
  private Identity                   identity;

  private LeadCaptureSettingsService service;

  @Before
  public void setUp() {
    service = new LeadCaptureSettingsService(settingService, organizationService, userACL);
  }

  @Test
  public void canManageSettingsTrueForAdministrator() {
    when(userACL.isAdministrator(identity)).thenReturn(true);

    assertTrue(service.canManageSettings(identity));
  }

  @Test
  public void canManageSettingsTrueForManagerOfTheSettingsPageGroup() {
    when(userACL.isAdministrator(identity)).thenReturn(false);
    when(userACL.hasPermission(identity, "manager:" + USERS_EXPERENCE_GROUP_NAME)).thenReturn(true);

    assertTrue(service.canManageSettings(identity));
  }

  @Test
  public void canManageSettingsFalseForOtherUsers() {
    when(userACL.isAdministrator(identity)).thenReturn(false);
    when(userACL.hasPermission(identity, "manager:" + USERS_EXPERENCE_GROUP_NAME)).thenReturn(false);

    assertFalse(service.canManageSettings(identity));
  }

  @Test
  public void canManageSettingsFalseWithoutIdentity() {
    assertFalse(service.canManageSettings(null));
    verifyNoInteractions(userACL);
  }

  @Test
  public void hasLeadCaptureStatusesOnlyForTheExactStatusesInOrder() {
    assertTrue(LeadCaptureSettingsService.hasLeadCaptureStatuses(statuses(Utils.LC_STATUSES)));
    assertFalse(LeadCaptureSettingsService.hasLeadCaptureStatuses(statuses("To Do", "In Progress", "Done")));
    assertFalse(LeadCaptureSettingsService.hasLeadCaptureStatuses(statuses("Attempted",
                                                                           "Open",
                                                                           "Contacted",
                                                                           "Qualified",
                                                                           "Recycled",
                                                                           "Accepted")));
    assertFalse(LeadCaptureSettingsService.hasLeadCaptureStatuses(null));
  }

  @Test
  public void toHtmlTextEscapesMarkupAndKeepsText() {
    assertEquals("&lt;img src=x onerror=alert(1)&gt;", Utils.toHtmlText("<img src=x onerror=alert(1)>"));
    assertEquals("Ren&eacute; &amp; Co", Utils.toHtmlText("Ren&eacute; & Co"));
    assertEquals("Ren&eacute;", Utils.toHtmlText("René"));
    assertEquals("", Utils.toHtmlText(null));
  }

  @Test
  public void getSettingsReturnsDefaultsWhenStoreReturnsNull() {
    when(settingService.get(LEAD_CAPTURE_CONTEXT, LEAD_CAPTURE_SCOPE, LEAD_CAPTURE_SETTINGS_KEY_NAME)).thenReturn(null);

    LeadCaptureSettings settings = service.getSettings();

    assertEquals(USERS_EXPERENCE_GROUP_NAME, settings.getUserExperienceGroup());
    assertNull(settings.getCaptureToken());
    assertNull(settings.getUserExperienceSpace());
    assertFalse(settings.isCaptureEnabled());
    assertFalse(settings.isMailingEnabled());
  }

  @Test
  public void getSettingsMapsStoredJson() {
    stubStoredSettings(storedSettings());

    LeadCaptureSettings settings = service.getSettings();

    assertEquals(UX_SPACE, settings.getUserExperienceSpace());
    assertEquals(PROJECT, settings.getLeadTaskProject());
    assertEquals(TOKEN, settings.getCaptureToken());
    assertEquals(SENDER, settings.getSenderMail());
    assertEquals(BLACKLIST, settings.getMailsBlackList());
    assertEquals(USERS_EXPERENCE_GROUP_NAME, settings.getUserExperienceGroup());
    assertTrue(settings.isCaptureEnabled());
    assertTrue(settings.isMailingEnabled());
  }

  /**
   * Regression test: a transient {@code null} from the store (the platform
   * SettingService swallows read failures) must not be remembered. The next
   * read has to reach the store again and return the real settings.
   */
  @Test
  public void getSettingsReadsThroughAfterTransientNullFromStore() {
    doReturn(null, SettingValue.create(json(storedSettings()))).when(settingService)
                                                                .get(LEAD_CAPTURE_CONTEXT,
                                                                     LEAD_CAPTURE_SCOPE,
                                                                     LEAD_CAPTURE_SETTINGS_KEY_NAME);

    LeadCaptureSettings afterFailure = service.getSettings();
    LeadCaptureSettings afterRecovery = service.getSettings();

    assertNull("a failed read yields default settings", afterFailure.getCaptureToken());
    assertEquals("the next read must see the stored settings again", TOKEN, afterRecovery.getCaptureToken());
    assertEquals(UX_SPACE, afterRecovery.getUserExperienceSpace());
    assertTrue(afterRecovery.isCaptureEnabled());
    verify(settingService, times(2)).get(LEAD_CAPTURE_CONTEXT, LEAD_CAPTURE_SCOPE, LEAD_CAPTURE_SETTINGS_KEY_NAME);
  }

  @Test
  public void getSettingsReturnsFreshInstanceOnEveryCall() {
    stubStoredSettings(storedSettings());

    LeadCaptureSettings first = service.getSettings();
    first.setCaptureToken("mutated-by-caller");
    LeadCaptureSettings second = service.getSettings();

    assertEquals("a caller mutating its copy must not affect other callers", TOKEN, second.getCaptureToken());
  }

  @Test
  public void saveSettingsPersistsJsonAndNextReadSeesIt() throws Exception {
    // Same space and same project as the stored settings: no task project
    // status update is triggered, only the persistence path is exercised.
    stubStoredSettings(storedSettings());
    LeadCaptureSettings toSave = storedSettings();
    toSave.setCaptureToken(NEW_TOKEN);
    toSave.setMailsBlackList(null);

    service.saveSettings(toSave);

    ArgumentCaptor<SettingValue<?>> captor = ArgumentCaptor.forClass(SettingValue.class);
    verify(settingService).set(eq(LEAD_CAPTURE_CONTEXT),
                               eq(LEAD_CAPTURE_SCOPE),
                               eq(LEAD_CAPTURE_SETTINGS_KEY_NAME),
                               captor.capture());
    String persisted = String.valueOf(captor.getValue().getValue());
    assertTrue("persisted JSON must carry the new token: " + persisted, persisted.contains(NEW_TOKEN));
    assertFalse("persisted JSON must not carry the old token: " + persisted, persisted.contains(TOKEN));

    doReturn(captor.getValue()).when(settingService).get(any(), any(), eq(LEAD_CAPTURE_SETTINGS_KEY_NAME));
    LeadCaptureSettings reloaded = service.getSettings();
    assertEquals(NEW_TOKEN, reloaded.getCaptureToken());
    assertEquals(UX_SPACE, reloaded.getUserExperienceSpace());
    assertNull("a field cleared by the save must read back as cleared", reloaded.getMailsBlackList());
  }

  @Test(expected = IllegalArgumentException.class)
  public void saveSettingsRejectsNull() throws Exception {
    service.saveSettings(null);
  }

  @Test
  public void isUxTeamManagerTrueForManagerOfConfiguredGroup() throws Exception {
    stubStoredSettings(storedSettings());
    when(organizationService.getMembershipHandler()).thenReturn(membershipHandler);
    when(membershipHandler.findMembershipByUserGroupAndType(USER, USERS_EXPERENCE_GROUP_NAME, "manager")).thenReturn(membership);

    assertTrue(service.isUxTeamManager(USER));
  }

  @Test
  public void isUxTeamManagerFalseForPlainMember() throws Exception {
    stubStoredSettings(storedSettings());
    when(organizationService.getMembershipHandler()).thenReturn(membershipHandler);
    when(membershipHandler.findMembershipByUserGroupAndType(USER, USERS_EXPERENCE_GROUP_NAME, "manager")).thenReturn(null);

    assertFalse(service.isUxTeamManager(USER));
  }

  @Test
  public void isUxTeamManagerChecksTheConfiguredGroup() throws Exception {
    LeadCaptureSettings stored = storedSettings();
    stored.setUserExperienceGroup(OTHER_GROUP);
    stubStoredSettings(stored);
    when(organizationService.getMembershipHandler()).thenReturn(membershipHandler);
    when(membershipHandler.findMembershipByUserGroupAndType(USER, OTHER_GROUP, "manager")).thenReturn(membership);

    assertTrue(service.isUxTeamManager(USER));
    verify(membershipHandler).findMembershipByUserGroupAndType(USER, OTHER_GROUP, "manager");
  }

  @Test
  public void isUxTeamManagerFallsBackToDefaultGroupWhenNothingStored() throws Exception {
    when(settingService.get(LEAD_CAPTURE_CONTEXT, LEAD_CAPTURE_SCOPE, LEAD_CAPTURE_SETTINGS_KEY_NAME)).thenReturn(null);
    when(organizationService.getMembershipHandler()).thenReturn(membershipHandler);
    when(membershipHandler.findMembershipByUserGroupAndType(USER, USERS_EXPERENCE_GROUP_NAME, "manager")).thenReturn(membership);

    assertTrue(service.isUxTeamManager(USER));
  }

  @Test
  public void isUxTeamManagerFalseWhenMembershipLookupFails() throws Exception {
    stubStoredSettings(storedSettings());
    when(organizationService.getMembershipHandler()).thenReturn(membershipHandler);
    when(membershipHandler.findMembershipByUserGroupAndType(USER,
                                                            USERS_EXPERENCE_GROUP_NAME,
                                                            "manager")).thenThrow(new Exception("IDM unavailable"));

    assertFalse(service.isUxTeamManager(USER));
  }

  @Test
  public void isUxTeamManagerFalseForBlankUser() {
    assertFalse(service.isUxTeamManager(null));
    assertFalse(service.isUxTeamManager(" "));
    verifyNoInteractions(settingService, organizationService);
  }

  private void stubStoredSettings(LeadCaptureSettings stored) {
    doReturn(SettingValue.create(json(stored))).when(settingService)
                                               .get(LEAD_CAPTURE_CONTEXT, LEAD_CAPTURE_SCOPE, LEAD_CAPTURE_SETTINGS_KEY_NAME);
  }

  private static LeadCaptureSettings storedSettings() {
    LeadCaptureSettings settings = new LeadCaptureSettings();
    settings.setUserExperienceSpace(UX_SPACE);
    settings.setLeadTaskProject(PROJECT);
    settings.setUserExperienceGroup(USERS_EXPERENCE_GROUP_NAME);
    settings.setUserExperienceBotUserName("leadbot");
    settings.setCaptureToken(TOKEN);
    settings.setSenderMail(SENDER);
    settings.setMailsBlackList(BLACKLIST);
    settings.setCaptureEnabled(true);
    settings.setMailingEnabled(true);
    return settings;
  }

  private static List<StatusDto> statuses(String... names) {
    return Arrays.stream(names).map(name -> new StatusDto(0, name)).toList();
  }

  private static String json(LeadCaptureSettings settings) {
    return Utils.toJsonString(settings);
  }
}
