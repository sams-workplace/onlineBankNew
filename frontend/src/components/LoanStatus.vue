<template>

<v-data-table
    :headers="headers"
    :items="loanStatus"
    :items-per-page="5"
    class="elevation-1"
  ></v-data-table>

</template>

<script>
  const axios = require('axios').default;

  export default {
    name: 'LoanStatus',
    props: {
      value: Object,
      editMode: Boolean,
      isNew: Boolean
    },
    data: () => ({
        headers: [
            { text: "id", value: "id" },
            { text: "loanRequestId", value: "loanRequestId" },
            { text: "requestId", value: "requestId" },
            { text: "requestName", value: "requestName" },
            { text: "requestDate", value: "requestDate" },
            { text: "procId", value: "procId" },
            { text: "procName", value: "procName" },
            { text: "procDate", value: "procDate" },
            { text: "loanStatus", value: "loanStatus" },
            { text: "amountOfMoney", value: "amountOfMoney" },
        ],
        loanStatus : [],
    }),
    async created() {
      var temp = await axios.get(axios.backend + '/loanstatuses')

      this.loanStatus = temp.data._embedded.loanstatuses;

    },
    methods: {
    }
  }
</script>

