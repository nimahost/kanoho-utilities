<script lang="ts">
	import { editor } from "$lib/monaco";
	import "greset";
</script>

<div>
	<div class="container" {@attach editor}></div>
</div>

<style lang="scss">
	.container {
		width: 100%;
		height: 100vh;
	}
</style>
