<template>
<v-app class="VuetifyApp" color="transaprent" id="leadCaptureSettingsApp">
    <main>
        <div :class="alert_type" class="alert" id v-if="alert">
            <i :class="alertIcon"></i>
            {{message}}
        </div>
        <v-flex>
            <v-container id="template">
                <v-card outlined style="padding: 16px;">
                    <v-form ref="form" v-model="valid">
                        <v-text-field v-model="setting.userExperienceGroup" :rules="[rules.required]" :label="$t('exoplatform.LeadCapture.leadSettings.uxGroup','UX team Group')" required></v-text-field>
                        <v-text-field v-model="setting.userExperienceSpace" :rules="[rules.required]" :label="$t('exoplatform.LeadCapture.leadSettings.leadCaptureSpace','Lead Capture Space')" required></v-text-field>
                        <v-text-field v-model="setting.leadTaskProject" :label="$t('exoplatform.LeadCapture.leadSettings.leadTaskProject','Lead Task Project')"></v-text-field>
                        <v-text-field v-model="setting.userExperienceBotUserName" :rules="[rules.required]" :label="$t('exoplatform.LeadCapture.leadSettings.botUserName','Bot User Name')" required></v-text-field>
                        <v-text-field v-model="setting.senderMail" :rules="[rules.required]" :label="$t('exoplatform.LeadCapture.leadSettings.senderMailAdress','Sender mail adress')" required></v-text-field>
                        <v-text-field v-model="setting.resourcesIdentifier" :label="$t('exoplatform.LeadCapture.leadSettings.resourcesIdentifiers','Resources identifiers')"></v-text-field>
                        <v-text-field v-model="setting.autoOpeningForms" :label="$t('exoplatform.LeadCapture.leadSettings.autoOpeningForms','Auto opening forms')"></v-text-field>
                        <v-text-field v-model="setting.mailsBlackList" :label="$t('exoplatform.LeadCapture.leadSettings.mailsBlackList','Mails Black List')"></v-text-field>
                        <v-text-field v-model="setting.allowedCaptureSourceDomain" :rules="[rules.required]" :label="$t('exoplatform.LeadCapture.leadSettings.allowedLeadSourceDomain','Allowed lead Source Domain')" required></v-text-field>
                        <v-text-field v-model="setting.captureToken" :rules="[rules.required]" :label="$t('exoplatform.LeadCapture.leadSettings.leadCaptureSecurityToken','Lead capture security token')" required></v-text-field>
                        <v-text-field v-model="setting.unsubscribeUrl" :label="$t('exoplatform.LeadCapture.leadSettings.unsubscribeUrl','Unsubscribe Url')"></v-text-field>
                        <v-text-field v-model="setting.leadManagementAppUrl" :rules="[rules.required]" :label="$t('exoplatform.LeadCapture.leadSettings.LeadManagementUrl','Lead Management Application Url')"></v-text-field>
                        <v-row>
                            <v-col cols="12" sm="6" md="5">
                                <v-switch v-model="setting.captureEnabled" :label="$t('exoplatform.LeadCapture.leadSettings.captureEnabled','Capture Enabled')"></v-switch>
                            </v-col>
                            <v-col cols="12" sm="6" md="5">
                                <v-switch v-model="setting.mailingEnabled" :label="$t('exoplatform.LeadCapture.leadSettings.mailingEnabled','Mailing Enabled')"></v-switch>
                            </v-col>
                        </v-row>
                        <v-card-actions>
                            <div class="flex-grow-1"></div>
                            <div class="uiAction">
                                <button :disabled="!valid" @click="save()" class="btn btn-primary" type="button">{{$t('exoplatform.LeadCapture.leadManagement.save','Save')}}
                                </button>
                                <button @click="cancel()" class="btn" type="button">{{$t('exoplatform.LeadCapture.leadManagement.close','Close')}}
                                </button>
                            </div>
                        </v-card-actions>
                    </v-form>
                </v-card>
            </v-container>
        </v-flex>
    </main>
</v-app>
</template>

<script>
export default {
    data: () => ({
        alert: false,
        message: '',
        alert_type: '',
        alertIcon: '',
        valid: true,
        setting: {},
        rules: {
            required: value => !!value || 'Required.',
            counter: value => value.length >= 3 || 'Min 3 characters',
        }
    }),
    created() {
        this.initialize()
    },
    methods: {

        initialize() {
            fetch(`/portal/rest/leadcapture/lcsettings/settings`, {
                    credentials: 'include',
                })
                .then((resp) => resp.json())
                .then((resp) => {
                    this.setting = resp;
                });
        },

        cancel() {
            this.initialize()
        },

        save() {
            fetch(`/portal/rest/leadcapture/lcsettings/settings`, {
                    method: 'post',
                    credentials: 'include',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(this.setting),
                })
                .then((result) => {
                    if (!result.ok) {
                        throw result;
                    }
                })
                .then((response) => {
                    this.initialize();
                    this.displaySusccessMessage('Settings updated');
                })
                .catch((result) => {
                    this.initialize();
                    result.text().then((body) => {
                        this.displayErrorMessage(body);
                    });
                });
        },
        displaySusccessMessage(message) {
            this.message = message;
            this.alert_type = 'alert-success';
            this.alertIcon = 'uiIconSuccess';
            this.alert = true;
            setTimeout(() => (this.alert = false), 5000);
        },
        displayErrorMessage(message) {
            this.isUpdating = false;
            this.message = message;
            this.alert_type = 'alert-error';
            this.alertIcon = 'uiIconError';
            this.alert = true;
            setTimeout(() => (this.alert = false), 5000);
        }

    }
};
</script>

<style>
#leadCaptureSettingsApp {
    overflow: hidden;
}

select {
    width: auto;
}

#leadCaptureSettingsApp .v-input input {
    margin-bottom: 0;
    border: 0;
    box-shadow: none;
}

#leadCaptureSettingsApp .v-toolbar .v-input {
    margin-left: 18px;
}
</style>
