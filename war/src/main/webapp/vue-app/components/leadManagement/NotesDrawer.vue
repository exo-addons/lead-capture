<template>
<v-container class="">

    <v-row class="mx-0 drawerHeader">
        <v-list-item>
            <v-list-item-content>
                <span class="drawerTitle">
                    {{$t('exoplatform.LeadCapture.leadManagement.task','Lead Task')}}
                </span>
            </v-list-item-content>
            <v-list-item-action class="drawerIcons">
                <v-menu offset-y>
                    <template v-slot:activator="{ on }">
                        <v-btn class="text-uppercase caption primary--text tasksBtn" outlined v-on="on">
                            {{$t(`exoplatform.LeadCapture.status.${lead.status}`,lead.status)}}
                        </v-btn>
                    </template>
                    <v-list>
                        <v-list-item v-for="(item, index) in statusList" :key="index" @click="changeStatus(item)">
                            <v-list-item-title>{{ item.text }}</v-list-item-title>
                        </v-list-item>
                    </v-list>
                </v-menu>
                <v-btn text small @click="addNote()">
                    <v-icon>mdi-plus</v-icon>
                    {{$t('exoplatform.LeadCapture.leadManagement.addComment','Add Comment')}}
                </v-btn>
                <v-btn small icon @click="navigateTo(`tasks/taskDetail/${lead.taskId}`)">
                    <v-icon>mdi-open-in-new</v-icon>
                </v-btn>
                <i class="actionIcon  uiCloseIcon" @click="toggleDrawer()"></i>
            </v-list-item-action>
        </v-list-item>
    </v-row>

    <v-divider></v-divider>
    <div class="drawerContent">
        <v-card v-show="dialog">
            <v-card-text>
                <ck-editor ref="ck" :content="content" />
            </v-card-text>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn @click="close()" color="blue darken-1" text> {{$t('exoplatform.LeadCapture.leadManagement.cancel','Cancel')}}</v-btn>
                <v-btn @click="saveNote()" color="blue darken-1" text> {{$t('exoplatform.LeadCapture.leadManagement.save','Save')}}</v-btn>
            </v-card-actions>
        </v-card>
        <v-list>
            <v-list-item :key="item.comment" v-for="item in comments">
                <div class="commentItem ">

                    <div class="commmentLeft pull-left">
                        <a class="avatarXSmall" :href="`/portal/intranet/profile/${item.author}`">
                            <img :src="`/rest/v1/social/users/${item.author}/avatar`" :alt="item.authorName">
                        </a>
                    </div>
                    <div class="commentRight">
                        <div class="author">
                            <a :href="`/portal/intranet/profile/${item.author}`">{{item.authorName}}</a>
                            <span class="date"> {{item.createdDate}} </span>
                        </div>
                        <span class="contentComment clearfix">
                            <div v-html="item.comment"></div>
                        </span>
                    </div>

                </div>

            </v-list-item>
        </v-list>
    </div>
</v-container>
</template>

<script>
import ckEditor from '../commons/ckEditor.vue';

export default {
    components: {
        ckEditor
    },
    props: ['lead', 'comments'],
    data: () => ({
        dialog: false,
        content: ""
    }),
    computed: {
        statusList() {
            return [{
                    text: this.$t('exoplatform.LeadCapture.status.Open'),
                    value: 'Open'
                },
                {
                    text: this.$t('exoplatform.LeadCapture.status.Attempted'),
                    value: 'Attempted'
                },
                {
                    text: this.$t('exoplatform.LeadCapture.status.Contacted'),
                    value: 'Contacted'
                },
                {
                    text: this.$t('exoplatform.LeadCapture.status.Qualified'),
                    value: 'Qualified'
                },
                {
                    text: this.$t('exoplatform.LeadCapture.status.Recycled'),
                    value: 'Recycled'
                },
                {
                    text: this.$t('exoplatform.LeadCapture.status.Accepted'),
                    value: 'Accepted'
                },
                {
                    text: this.$t('exoplatform.LeadCapture.status.Bad_Data'),
                    value: 'Bad_Data'
                },
                {
                    text: this.$t('exoplatform.LeadCapture.status.Duplicate'),
                    value: 'Duplicate'
                }
            ]
        },
    },
    methods: {
        toggleDrawer() {
            this.$emit('toggleDrawer');
        },
        navigateTo(pagelink) {
            window.open(`${ eXo.env.portal.context }/${ eXo.env.portal.portalName }/${ pagelink }`, '_blank');
        },

        addNote() {
            this.dialog = true
        },
        close() {
            this.newNote = {}
            this.dialog = false
        },
        changeStatus(item) {
            this.$emit('changeStatus', item);
        },
        saveNote() {
            if (this.$refs.ck !== undefined) {

                fetch(`/portal/rest/leadcapture/leadsmanagement/comments/` + this.lead.taskId, {
                        method: 'post',
                        credentials: 'include',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        body: this.$refs.ck.getContent(),
                    })
                    .then((resp) => resp.json())
                    .then((resp) => {
                        this.comments.unshift(resp);
                    });
                this.$refs.ck.setContent("")
                this.dialog = false
            }

        }
    }
}
</script>

<style>
.commentItem {
    width: 100%;
}

.avatarXSmall {
    max-height: 36px;
    max-width: 36px;
}

.commmentLeft+.commentRight {
    margin-left: 50px;
}

.date {
    margin-left: 10px;
    font-size: 12px;
    padding-right: 16px;
    float: right;
}

.comment {
    width: 100%;
}

.statusBtn {
    min-width: 30% !important;
}
</style>
