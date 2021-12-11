states = ['AL', 'AK', 'AZ', 'AR', 'CA', 'CO', 'CT', 'DE', 'FL', 'GA', 'ID', 'IL', 'HI',
          'IN', 'IA', 'KS', 'KY', 'LA', 'ME', 'MD', 'MA', 'MI', 'MN', 'MS', 'MO',
          'MT', 'NE', 'NV', 'NH', 'NJ', 'NM', 'NY', 'NC', 'ND', 'OH', 'OK', 'OR',
          'PA', 'RI', 'SC', 'SD', 'TN', 'TX', 'UT', 'VT', 'VA', 'WA', 'WV', 'WI', 'WY']

file = open("CountWise_sorted.csv", 'r')
file_out = open('state_count.csv', 'w')
file_out.write('State,Count\n')
line = file.readline()
data = []
while True:
    line = file.readline()
    if not line:
        break
    line = line.strip().split(',')
    if line[0] in states:
        file_out.write(line[0]+','+line[1]+'\n')
file.close()
file_out.close()



