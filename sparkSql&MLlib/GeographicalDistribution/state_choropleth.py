import geopandas
import pandas
from matplotlib import pyplot as plt


GEOJSON_FILE = "USStates.geojson"
CSV_FILE = "state_count.csv"

geojson = geopandas.read_file(GEOJSON_FILE)
data = pandas.read_csv(CSV_FILE)
full_dataset = geojson.merge(data, left_on="STATE_ID", right_on="State")
df = full_dataset.drop(["Name", "STATE_ID", "FID"], axis = 1)
sorted_df = df.sort_values(by=["Count"], ascending=False)

title = "StackOverflow usage based on posts in US by states"
col = 'Count'
vmin = sorted_df[col].min()
vmax = sorted_df[col].max()
cmap = 'viridis'

fig, ax = plt.subplots(1, figsize=(20, 8))

ax.axis('off')
sorted_df.plot(column=col, ax=ax, edgecolor='0.8', linewidth=1, cmap=cmap)
ax.set_title(title, fontdict={'fontsize': '25', 'fontweight':'3'})
sm = plt.cm.ScalarMappable(norm=plt.Normalize(vmin=vmin, vmax=vmax), cmap=cmap)
sm._A = []
cbaxes = fig.add_axes([0.15, 0.25, 0.01, 0.4])
cbar = fig.colorbar(sm, cax=cbaxes)
fig.savefig('usage_usa.png')