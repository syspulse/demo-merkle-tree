<script>
	import Node from "./Node.svelte";
	
	import { createEventDispatcher } from 'svelte';

	const dispatch = createEventDispatcher();

	export let nodes = [];
	export let xpos = 0;
	export let ypos = 10;
	export let width = 1200;
	export let height = 1000;

	export let x_offset = 5;
	export let y_offset = 10;
	export let node_width = 130;
	export let node_height = 50;

	let tree = [];

	$: { 
		const depth = Math.floor(Math.log2(nodes.length));
		const w_last = (node_width + x_offset) * Math.pow(2,depth);
		console.log("depth=",depth,"w_last=",w_last);
		tree = [];
		for (let i = 0; i < nodes.length; i++) {
			const level = Math.floor(Math.log2(i+1));
			const parent = Math.floor((i-1) / 2);
			const bucket = w_last / (Math.pow(2,level));
			
			const x_direction = i % 2 == 0 ? 1 : -1;
			
			const x = (i - (Math.pow(2,level) - 1)) * bucket + bucket / 2;
			
			console.log("i="+i,"level=",level,"parent="+parent,"bucket=",bucket,"x=",x);

			tree[i] = {
				name: nodes[i].name + "-L(" + level+")-P("+parent+")",
				icon: depth == level ? "file" : "",
				data: nodes[i].data,
				x: x,
				y: (level) * (node_height + y_offset),
				w: node_width,
				h: node_height,
				x_parent: parent>=0 ? tree[parent].x : -1,
				y_parent: parent>=0 ? tree[parent].y  : -1,
				level: level
			}
		};
	}

</script>

<style>
	.dashboard {
		fill: rgb(235, 237, 240);
		stroke-width: 1;
		stroke: rgb(88, 96, 105);
		overflow: scroll;
	}
	.link {
		stroke:rgb(88, 96, 105);
		stroke-width:2;
		stroke-linecap:"round";
		stroke-linejoin: "round";
		stroke-dasharray: 3;
		fill:"none";
	}
	.dash {
		/* position:fixed;  */
		/* overflow: scroll; */
		height:100%; 
		width:100%
	}
</style>


<!-- <svg viewBox="0 0 {width} {height}" overflow="scroll" preserveAspectRatio="none"> -->
<svg class="dash">
	<!-- <g transform="translate(0,0)">
		<rect x="0" y="0" {width} {height} class="dashboard">
			<title>tree</title>
		</rect>
	</g> -->

	{#each tree as t, i}
		<Node
			name={t.name}
			icon={t.icon}
			xpos={xpos+t.x}
			ypos={ypos+t.y}
			w={t.w}
			h={t.h}
			data={t.data}
			x_parent={t.x_parent}
			y_parent={t.y_parent}
			level = {t.level}
			 />

		{#if t.x_parent!=-1}
			 <path class="link" d="M {xpos+t.x+t.w/2} {ypos+t.y} L {xpos + t.x_parent + t.w/2} {ypos+t.y_parent+t.h}"/>
		 {/if}
	{/each}
</svg>
