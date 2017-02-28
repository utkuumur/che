'use strict';

exports.dockerimageStack = function () {

  angular.module('userDashboardMock', ['userDashboard', 'ngMockE2E'])
    .run(['$httpBackend', 'cheAPIBuilder', 'cheHttpBackendProvider', ($httpBackend, cheAPIBuilder, cheHttpBackendProvider) => {

      let stackId = 'testStackId',
          stackName = 'testStack',
          stackWorkspaceConfig = {
            'environments': {
              'default': {
                'machines': {
                  'dev-machine': {
                    'servers': {},
                    'agents': ['org.eclipse.che.terminal', 'org.eclipse.che.ws-agent', 'org.eclipse.che.ssh'],
                    'attributes': {'memoryLimitBytes': '2147483648'}
                  }
                }, 'recipe': {'location': 'eclipse/node', 'type': 'dockerimage'}
              }
            }, 'commands': [], 'projects': [], 'defaultEnv': 'default', 'name': 'default', 'links': []
          };

      let stack = cheAPIBuilder.getStackBuilder().withId(stackId).withName(stackName).withWorkspaceConfig(stackWorkspaceConfig).build();

      let cheBackend = cheHttpBackendProvider.buildBackend($httpBackend, cheAPIBuilder);

      cheBackend.addStacks([stack]);
      cheBackend.setup();

    }]);

};

exports.dockerfileStack = function () {

  angular.module('userDashboardMock', ['userDashboard', 'ngMockE2E'])
    .run(['$httpBackend', 'cheAPIBuilder', 'cheHttpBackendProvider', ($httpBackend, cheAPIBuilder, cheHttpBackendProvider) => {

      let stackId = 'testStackId',
          stackName = 'testStack',
          stackWorkspaceConfig = {
            'environments': {
              'default': {
                'recipe': {
                  'contentType': 'text/x-dockerfile',
                  'type': 'dockerfile',
                  'content': 'FROM eclipse/node\n'
                },
                'machines': {
                  'dev-machine': {
                    'servers': {},
                    'agents': ['org.eclipse.che.ws-agent', 'org.eclipse.che.ssh', 'org.eclipse.che.terminal'],
                    'attributes': {'memoryLimitBytes': '2147483648'}
                  }
                }
              }
            }, 'commands': [], 'projects': [], 'defaultEnv': 'default', 'name': 'default', 'links': []
          };

      let stack = cheAPIBuilder.getStackBuilder().withId(stackId).withName(stackName).withWorkspaceConfig(stackWorkspaceConfig).build();

      let cheBackend = cheHttpBackendProvider.buildBackend($httpBackend, cheAPIBuilder);

      cheBackend.addStacks([stack]);
      cheBackend.setup();

    }]);

};

exports.composefileStack = function () {

  angular.module('userDashboardMock', ['userDashboard', 'ngMockE2E'])
    .run(['$httpBackend', 'cheAPIBuilder', 'cheHttpBackendProvider', ($httpBackend, cheAPIBuilder, cheHttpBackendProvider) => {

      let stackId = 'testStackId',
          stackName = 'testStack',
          stackWorkspaceConfig = {
            'environments': {
              'default': {
                'recipe': {
                  'contentType': 'application/x-yaml',
                  'type': 'compose',
                  'content': 'services:\n  db:\n    image: mysql\n    environment:\n      MYSQL_ROOT_PASSWORD: password\n      MYSQL_DATABASE: petclinic\n      MYSQL_USER: petclinic\n      MYSQL_PASSWORD: password\n    mem_limit: 1073741824\n  dev-machine:\n    image: eclipse/ubuntu_jdk8\n    mem_limit: 2147483648\n    depends_on:\n      - db\n'
                },
                'machines': {
                  'db': {
                    'servers': {},
                    'agents': [
                      'org.eclipse.che.terminal'
                    ],
                    'attributes': {
                      'memoryLimitBytes': 1073741824
                    }
                  },
                  'dev-machine': {
                    'servers': {},
                    'agents': [
                      'org.eclipse.che.terminal',
                      'org.eclipse.che.ws-agent',
                      'org.eclipse.che.ssh'
                    ],
                    'attributes': {
                      'memoryLimitBytes': 2147483648
                    }
                  }
                }
              }
            }, 'commands': [], 'projects': [], 'defaultEnv': 'default', 'name': 'default', 'links': []
          };

      let stack = cheAPIBuilder.getStackBuilder().withId(stackId).withName(stackName).withWorkspaceConfig(stackWorkspaceConfig).build();

      let cheBackend = cheHttpBackendProvider.buildBackend($httpBackend, cheAPIBuilder);

      cheBackend.addStacks([stack]);
      cheBackend.setup();

    }]);

};
