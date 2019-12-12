<template>
    <v-container class="">
        <v-list-item>
            <v-list-item-title>
                NOTES
            </v-list-item-title>
            <a @click="addNote()" class="actionIcon" role="button" style="float: right;"> <i
                    class="uiIconPlus uiIconLightGray"></i> </a>
        </v-list-item>
        <v-list elevation="2">
            <v-list-item :key="item.noteMessage" v-for="item in notes">
                <v-list-item-content>
                    <v-list-item-title>{{ item.noteMessage }}</v-list-item-title>
                    <v-list-item-subtitle>{{ item.noteMessageDate }}</v-list-item-subtitle>
                    <v-divider></v-divider>
                </v-list-item-content>
            </v-list-item>
        </v-list>

        <v-dialog max-width="600px" persistent v-model="dialog">
            <v-card>
                <v-card-title>
                    <span class="headline">New note</span>
                </v-card-title>
                <v-card-text>
                    <v-container>
                        <v-row>
                            <v-col>
                                <v-textarea label="Solo textarea" name="input-7-4" solo
                                            v-model="newNote.noteMessage"></v-textarea>
                            </v-col>
                        </v-row>
                    </v-container>
                </v-card-text>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn @click="close()" color="blue darken-1" text>Close</v-btn>
                    <v-btn @click="saveNote()" color="blue darken-1" text>Save</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </v-container>

</template>
<script>
  export default {
     props: ['notes'],
     data: () => ({
      dialog: false,
      newNote:{}
    }),
     methods:{
       addNote(){
        this.dialog=true
       },
       close(){
        this.newNote={}
        this.dialog=false
       },
       saveNote(){
         this.newNote.noteMessageDate="15/12/2010";
         this.notes.push(this.newNote)
         this.newNote={}
        this.dialog=false
       }
     }
  }


</script>