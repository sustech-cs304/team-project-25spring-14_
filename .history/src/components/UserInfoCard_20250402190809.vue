<template>
  <div class="user-info-card">
    <div class="card-header">
      <img :src="user.avatar" alt="User Avatar" class="avatar" />
      <h2 class="username">{{ user.name }}</h2>
    </div>
    <div class="card-body">
      <p class="email">Email: {{ user.email }}</p>
      <p class="bio">{{ user.bio }}</p>
    </div>
    <div class="card-footer">
      <router-link :to="`/user/${user.id}/albums`" class="link"
        >View Albums</router-link
      >
      <router-link :to="`/user/${user.id}/space`" class="link"
        >Visit Space</router-link
      >
    </div>
  </div>
</template>

<script>
export default {
  name: "UserInfoCard",
  props: {
    user: {
      type: Object,
      required: true,
    },
  },
};
</script>

<style scoped>
.user-info-card {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 16px;
  max-width: 400px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  margin-right: 16px;
}

.username {
  font-size: 1.5em;
  margin: 0;
}

.card-body {
  margin-bottom: 16px;
}

.email {
  font-size: 0.9em;
  color: #555;
}

.bio {
  font-size: 1em;
  color: #333;
}

.card-footer {
  display: flex;
  justify-content: space-between;
}

.link {
  text-decoration: none;
  color: #007bff;
  font-weight: bold;
}

.link:hover {
  text-decoration: underline;
}
</style>
npm