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
import {WorkspaceMachineConfigController} from '../machine-config.controller';

/**
 * @ngdoc controller
 * @name machine.config.controller:EditMachineNameDialogController
 * @description This class is handling the controller for the dialog box about editing the machine name.
 * @author Oleksii Kurinnyi
 */
export class EditMachineNameDialogController {
  private name: string;
  private origName: string;
  private machinesNames: string[];
  private callbackController: WorkspaceMachineConfigController;

  /**
   * Default constructor that is using resource
   * @ngInject for Dependency injection
   */
  constructor(private $mdDialog: ng.material.IDialogService) {
    this.$mdDialog = $mdDialog;

    this.origName = this.name;
  }

  isUnique(name) {
    if (name === this.origName) {
      return true;
    }
    return this.machinesNames.indexOf(name) < 0;
  }

  /**
   * It will hide the dialog box.
   */
  hide() {
    this.$mdDialog.hide();
  }

  /**
   * Update machine name
   */
  updateMachineName() {
    this.callbackController.updateMachineName(this.name);
    this.hide();
  }
}
