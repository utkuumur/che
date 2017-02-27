/*
 * Copyright (c) 2015-2017 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 */
'use strict';
import {CheWorkspace} from '../../../components/api/che-workspace.factory';
import {CheAPIBuilder} from '../../../components/api/builder/che-api-builder.factory';
import {CheHttpBackend} from '../../../components/api/test/che-http-backend';
import IdeSvc from '../../ide/ide.service';


/**
 * Test of the NavbarRecentWorkspacesController
 */
describe('NavbarRecentWorkspacesController', () => {
  /**
   * NavbarRecentWorkspacesController
   */
  let navbarRecentWorkspacesController;

  /**
   * API builder
   */
  let apiBuilder: CheAPIBuilder;

  /**
   * Backend for handling http operations
   */
  let httpBackend: ng.IHttpBackendService;

  /**
   * Che backend
   */
  let cheBackend: CheHttpBackend;


  let workspaces: Array<che.IWorkspace>;

  /**
   *  setup module
   */
  beforeEach(angular.mock.module('userDashboard'));

  /**
   * Inject factory and http backend
   */
  beforeEach(inject(($rootScope: ng.IRootScopeService, cheWorkspace: CheWorkspace, cheAPIBuilder: CheAPIBuilder, cheHttpBackend: CheHttpBackend, $controller: any, ideSvc: IdeSvc, $window: ng.IWindowService, $log: ng.ILogService) => {
    apiBuilder = cheAPIBuilder;
    cheBackend = cheHttpBackend;
    httpBackend = cheHttpBackend.getHttpBackend();

    let scope = $rootScope.$new();
    navbarRecentWorkspacesController = $controller('NavbarRecentWorkspacesController', {
      ideSvc: IdeSvc, cheWorkspace: cheWorkspace, $window: $window, $log: $log, $scope: scope, $rootScope: $rootScope
    });

    workspaces = [];
    for (let i = 0; i < 10; ++i) {
      let wrkspId = 'workspaceId' + i;
      let wrkspName = 'testName' + i;
      let wrkspCreateDate = new Date(2015, 1, i).toString();
      let wrkspAttr = {'created': Date.parse(wrkspCreateDate)};
      let workspace = apiBuilder.getWorkspaceBuilder().withId(wrkspId).withAttributes(wrkspAttr).withName(wrkspName).build();
      workspaces.push(workspace);
    }
    // shuffle the workspaces
    workspaces.sort(() => {
      return 0.5 - Math.random();
    });
    // providing request
    // add workspaces on Http backend
    cheBackend.addWorkspaces(workspaces);

    // setup backend
    cheBackend.setup();

    // fetch workspaces
    cheWorkspace.fetchWorkspaces();

    // flush command
    httpBackend.flush();
  }));

  /**
   * Check assertion after the test
   */
  afterEach(() => {
    httpBackend.verifyNoOutstandingExpectation();
    httpBackend.verifyNoOutstandingRequest();
  });

  /**
   * Check recent workspaces
   */
  it('Check very recent workspaces', inject(() => {
      // get recentWorkspaces
      let recentWorkspaces = navbarRecentWorkspacesController.getRecentWorkspaces();

      // check max length
      expect(recentWorkspaces.length).toEqual(5);

      // prepare test objects
      let testWorkspaces: Array<che.IWorkspace> = angular.copy(workspaces);
      testWorkspaces.sort((workspace1: che.IWorkspace, workspace2: che.IWorkspace) => {
        return workspace2.attributes.created - workspace1.attributes.created;
      });
      let veryRecentWorkspaceId = testWorkspaces[testWorkspaces.length - 1].id;
      testWorkspaces.splice(recentWorkspaces.length, testWorkspaces.length);

      // check default sorting
      let lastPosition = recentWorkspaces.length - 1;
      for (let i = 0; i < lastPosition; i++) {
        expect(recentWorkspaces[i].id).toEqual(testWorkspaces[i].id);
      }
      expect(recentWorkspaces[lastPosition].id).toEqual(testWorkspaces[lastPosition].id);
      expect(recentWorkspaces[lastPosition].id).not.toEqual(veryRecentWorkspaceId);

      // set veryRecentWorkspaceId
      navbarRecentWorkspacesController.updateRecentWorkspace(veryRecentWorkspaceId);
      recentWorkspaces = navbarRecentWorkspacesController.getRecentWorkspaces();

      // check sorting with veryRecentWorkspace
      for (let i = 0; i < lastPosition; i++) {
        expect(recentWorkspaces[i].id).toEqual(testWorkspaces[i].id);
      }
      expect(recentWorkspaces[lastPosition].id).not.toEqual(testWorkspaces[lastPosition].id);
      expect(recentWorkspaces[lastPosition].id).toEqual(veryRecentWorkspaceId);
    })
  );
});
