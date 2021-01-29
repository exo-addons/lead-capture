package org.exoplatform.leadcapture.test.mock;


import java.util.List;
import java.util.Set;

import org.exoplatform.container.component.ComponentPlugin;
import org.exoplatform.services.organization.*;


public class MockOrgService implements OrganizationService {


  @Override
  public UserHandler getUserHandler() {
    return null;
  }

  @Override
  public UserProfileHandler getUserProfileHandler() {
    return null;
  }

  @Override
  public GroupHandler getGroupHandler() {
    return null;
  }

  @Override
  public MembershipTypeHandler getMembershipTypeHandler() {
    return null;
  }

  @Override
  public MembershipHandler getMembershipHandler() {
    return null;
  }

  @Override
  public void addListenerPlugin(ComponentPlugin componentPlugin) throws Exception {

  }
}
