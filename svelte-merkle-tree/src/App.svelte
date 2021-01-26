<script>
	import { onMount } from "svelte";
	import Process from './Node.svelte';
	import Icon, {iconNames} from "./Icon.svelte";
	import Dashboard from "./Dashboard.svelte";

	import sha256 from "js-sha256";
	import { initClient } from "@urql/svelte";
	import { operationStore, query } from "@urql/svelte";

	initClient({
		url: "http://localhost:8081/graphql"
	});
	
	console.log("icon",iconNames[getRand(iconNames.length)+1])
	let time = new Date();
	let tick = 1;

	let size = 7;

	function getRand(max) {
		return Math.abs(Math.floor(Math.random() * Math.floor(max)));
	}

	function init(size) {
		const a = new Array(Number(size)).fill();
		const depth = Math.floor(Math.log2(a.length));
		const initialNodes = a.map(
		(_,i) => {
			const d =  depth == Math.floor(Math.log2(i+1)) ? ""+i : "0x"+sha256(""+i);
			return {
				name: "n"+i,
				// icon: iconNames[getRand(iconNames.length)+1],
				data: d,
			}
		});

		
		return initialNodes;
	}

	let randomData = false;
	let nodes = init(size);
	let q_nodes;

	$: {
		const limit = size;
		const dataType = randomData ? "random" : "";
		q_nodes = operationStore(
			`
			query($limit: Int!,$dataType: String!){
					nodes(limit: $limit, data: $dataType) {
						data,
						name,
						type
					}
			}
			`, 
			{limit,dataType}
		);

		query(q_nodes);
		
		// nodes = init(size).map(
		// 	(node,i) => ({
		// 		name: node.name,
		// 		data: node.data,
		// 	})
		// );
	}

	function getWidth(id) {
		const element = document.getElementById(id);
		const positionInfo = element.getBoundingClientRect();
		const height = positionInfo.height;
		const width = positionInfo.width;
		return width;
	}
</script>

<style>
	.dashboard {
		border:1px solid black;	
		overflow: auto;
		height:80%; 
	}
</style>



<h1>Merkle Tree: {size}</h1>
<!-- <label>Number of nodes:<input bind:value={size} placeholder="size..."></label> -->
<label>Nodes:
	<input type=number bind:value={size} min=0 max=32>
	<input type=range bind:value={size} min=0 max=32>
</label>
<label>
	<input type=checkbox bind:checked={randomData}>
	Random
</label>
{#if $q_nodes.fetching}
    <div>Loading...</div>
{:else if $q_nodes.error}
	<div>Error: {$q_nodes.error.message}</div>
{:else if !$q_nodes.data}
    <div>No data</div>
{:else}
	<div id="dashboard" class="dashboard">
	<Dashboard 
		nodes = {q_nodes.data.nodes}
	/>
	</div>
{/if}

