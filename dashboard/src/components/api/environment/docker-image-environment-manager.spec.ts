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

import {DockerImageEnvironmentManager} from './docker-image-environment-manager';
import IWorkspaceEnvironment = _che.IWorkspaceEnvironment;
import {IEnvironmentManagerMachine, IEnvironmentManagerMachineServer} from './environment-manager-machine';

/**
 * Test the environment manager for docker image based recipes
 * @author Oleksii Kurinnyi
 */

describe('DockerImageEnvironmentManager', () => {
  let envManager: DockerImageEnvironmentManager, environment: IWorkspaceEnvironment, machines: IEnvironmentManagerMachine[];

  beforeEach(inject(($log: ng.ILogService) => {
    envManager = new DockerImageEnvironmentManager($log);

    environment = {
      'machines': {
        'dev-machine': {
          'servers': {
            '10240/tcp': {
              'properties': {},
              'protocol': 'http',
              'port': '10240'
            }
          }, 'agents': ['ws-agent', 'org.eclipse.che.ws-agent'], 'attributes': {'memoryLimitBytes': '16642998272'}
        }
      }, 'recipe': {'location': 'codenvy/ubuntu_jdk8', 'type': 'dockerimage'}
    };

    machines = envManager.getMachines(environment);
  }));

  it('cannot edit environment variables', () => {
    let canEditEnvVariables = envManager.canEditEnvVariables(machines[0]);

    expect(canEditEnvVariables).toBe(false);
  });

  it('should return source', () => {
    let source = envManager.getSource(machines[0]);

    let expectedSource = {image: environment.recipe.location};
    expect(source).toEqual(expectedSource);
  });

  it('should return servers', () => {
    let servers = envManager.getServers(machines[0]);

    let expectedServers = <IEnvironmentManagerMachineServer>environment.machines['dev-machine'].servers;
    Object.keys(expectedServers).forEach((serverRef: string) => {
      expectedServers[serverRef].userScope = true;
    });

    expect(servers).toEqual(expectedServers);
  });

  it('should return memory limit', () => {
    let memoryLimit = envManager.getMemoryLimit(machines[0]);

    let expectedMemoryLimit = environment.machines['dev-machine'].attributes.memoryLimitBytes;
    expect(memoryLimit).toEqual(expectedMemoryLimit);
  });

  it('the machine should be a dev machine', () => {
    let isDev = envManager.isDev(machines[0]);

    expect(isDev).toBe(true);
  });

  it('should update environment\'s recipe via machine\'s source', () => {
    let machines = envManager.getMachines(environment),
        newSource = 'eclipse/node';

    envManager.setSource(machines[0], newSource);
    let newEnvironment = envManager.getEnvironment(environment, machines);
    expect(newEnvironment.recipe.location).toEqual(newSource);
  });

});

