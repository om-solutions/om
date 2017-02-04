<html lang="en">
<head>
    <meta charset="UTF-8">
    <script src="https://code.jquery.com/jquery.min.js"></script>
    <script src="https://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.2/js/bootstrap.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.10.0/bootstrap-table.min.js"></script>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.10.0/bootstrap-table.min.css" rel="stylesheet" type="text/css" />
    <script type="text/javascript">
        $(document).ready(function () {
            //Sample Json data
            var jsondata = [
                {
                    "id": "1",
                    "Name": "Name1",
                    "Address": "Address1"
                },
                {
                    "id": "2",
                    "Name": "Name2",
                    "Address": "Address2"
                },
                {
                    "id": "3",
                    "Name": "Name3",
                    "Address": "Address3"
                },
                {
                    "id": "4",
                    "Name": "Name4",
                    "Address": "Address4"
                },
                {
                    "id": "5",
                    "Name": "Name5",
                    "Address": "Address5"
                }];
 
            $('#table').bootstrapTable({
                //Assigning data to table
                data: jsondata
            });
        });
    </script>
</head>
<body>
    <form id="form1" runat="server">
        <table id="table">
            <thead>
                <tr>
                    <th data-field="id">ID</th>
                    <th data-field="Name">Name</th>
                    <th data-field="Address">Address</th>
                </tr>
            </thead>
        </table>
    </form>
</body>
</html>