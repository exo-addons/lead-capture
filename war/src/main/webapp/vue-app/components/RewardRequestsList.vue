<template>
    <v-data-table
            :headers="headers"
            :items="requests"
            :items-per-page="5"
            class="elevation-1"
    ></v-data-table>
</template>

<script>
  export default {
      props: ['requests'],
    data () {
      return {
        headers: [
          { text: 'Reward Pack', value: 'rewardPack' },
          { text: 'Reward token', value: 'rewardToken' },
          { text: 'CompanyWallet', value: 'companyWallet' },
          { text: 'Date', value: 'rewardRequestDate' },
        ],
      }
    },
  }


</script>